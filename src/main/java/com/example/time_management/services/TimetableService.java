package com.example.time_management.services;

import com.example.time_management.models.Timetable;
import com.example.time_management.dto.Course;
import com.example.time_management.dto.TimetableEntry;
import com.example.time_management.dto.TimetableResponse;
import com.example.time_management.repositories.TimetableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TimetableService {
    private final TimetableRepository repository;
    private final ObjectMapper mapper;

    @Value("${timetable.termStartDate:2025-02-17}")
    private String TERM_START_DATE;

    private static final List<String> WEEKDAYS = Arrays.asList(
        "星期一","星期二","星期三","星期四","星期五","星期六","星期日"
    );
    private static final Pattern BLANKS = Pattern.compile("\\s+");

    public TimetableService(TimetableRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 解析 PDF 并保存到数据库，返回解析结果
     */
    public TimetableResponse parseAndSave(MultipartFile file, Long userId) throws IOException {
        File pdf = toFile(file);
        List<Map<String, String>> raw = extractRaw(pdf);
        List<Map<String, String>> cont = mergeContinuationLines(raw);
        List<Map<String, String>> paged = mergeMultiPage(cont);
        List<TimetableEntry> entries = normalize(paged);

        TimetableResponse resp = new TimetableResponse(TERM_START_DATE, entries);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);

        Timetable entity = new Timetable();
        entity.setUserId(userId);
        entity.setTermStartDate(TERM_START_DATE);
        entity.setJsonData(json);
        entity.setCreatedAt(LocalDateTime.now());
        repository.save(entity);

        return resp;
    }

    /**
     * 查询某用户所有课程表
     */
    public List<TimetableResponse> getByUserId(Long userId) throws IOException {
        List<Timetable> list = repository.findByUserId(userId);
        List<TimetableResponse> result = new ArrayList<>();
        for (Timetable t : list) {
            TimetableResponse tr = mapper.readValue(t.getJsonData(), TimetableResponse.class);
            result.add(tr);
        }
        return result;
    }

    // --- Private helpers ---

    private File toFile(MultipartFile file) throws IOException {
        File conv = File.createTempFile("timetable", ".pdf");
        try (FileOutputStream fos = new FileOutputStream(conv)) {
            fos.write(file.getBytes());
        }
        return conv;
    }

    private List<Map<String, String>> extractRaw(File pdf) throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        try (PDDocument doc = PDDocument.load(pdf)) {
            ObjectExtractor oe = new ObjectExtractor(doc);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            int pages = doc.getNumberOfPages();
            List<String> lastHeader = null;
            for (int i = 1; i <= pages; i++) {
                // 提取当前页
                Page page = oe.extract(i);
                List<Table> tables = sea.extract(page);
                if (tables.isEmpty()) continue;
                Table table = tables.get(0);
                List<List<RectangularTextContainer>> rawRows = table.getRows();

                // 找到表头行
                Integer hdr = null;
                for (int r = 0; r < rawRows.size(); r++) {
                    if ("时间段".equals(rawRows.get(r).get(0).getText())) { hdr = r; break; }
                }
                List<String> header;
                int start;
                if (hdr != null) {
                    header = rawRows.get(hdr).stream()
                                .map(RectangularTextContainer::getText)
                                .collect(Collectors.toList());
                    lastHeader = header;
                    start = hdr + 1;
                } else if (lastHeader != null) {
                    header = lastHeader;
                    start = 0;
                } else continue;

                // 读取数据行
                for (int r = start; r < rawRows.size(); r++) {
                    List<RectangularTextContainer> cells = rawRows.get(r);
                    if (cells.size() != header.size()) continue;
                    Map<String, String> map = new LinkedHashMap<>();
                    for (int c = 0; c < header.size(); c++) {
                        map.put(header.get(c), cells.get(c).getText());
                    }
                    rows.add(map);
                }
            }
        }
        return rows;
    }



    private List<Map<String, String>> mergeContinuationLines(List<Map<String, String>> data) {
        List<Map<String, String>> result = new ArrayList<>(data);
        List<String> cols = new ArrayList<>(data.get(0).keySet());
        for (int i = result.size() - 1; i >= 0; i--) {
            String sec = result.get(i).get("节次");
            if (sec == null || sec.isEmpty()) {
                boolean merged = false;
                for (String col : cols) {
                    if ("时间段".equals(col) || "节次".equals(col)) continue;
                    String txt = result.get(i).get(col);
                    if (txt == null || txt.isEmpty()) continue;
                    for (int j = i - 1; j >= 0; j--) {
                        String prev = result.get(j).get(col);
                        if (prev != null && !prev.isEmpty()) { result.get(j).put(col, prev + "；" + txt); merged = true; break; }
                    }
                }
                if (merged) result.remove(i);
            }
        }
        return result;
    }

    private List<Map<String, String>> mergeMultiPage(List<Map<String, String>> raw) {
        Map<String, Map<String, List<String>>> bucket = new LinkedHashMap<>();
        for (Map<String, String> entry : raw) {
            String key = entry.get("时间段") + "|" + entry.get("节次");
            bucket.computeIfAbsent(key, k -> new LinkedHashMap<>());
            Map<String, List<String>> grp = bucket.get(key);
            for (Map.Entry<String, String> e : entry.entrySet()) {
                String col = e.getKey(); String val = e.getValue();
                if ("时间段".equals(col) || "节次".equals(col)) continue;
                grp.computeIfAbsent(col, k -> new ArrayList<>()).add(val);
            }
        }
        List<Map<String, String>> merged = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<String>>> e : bucket.entrySet()) {
            String[] parts = e.getKey().split("\\|");
            Map<String, String> row = new LinkedHashMap<>();
            row.put("时间段", parts[0]); row.put("节次", parts[1]);
            e.getValue().forEach((col, lst) -> {
                List<String> flat = lst.stream()
                    .flatMap(s -> Arrays.stream(s.split("；")))
                    .distinct().collect(Collectors.toList());
                row.put(col, String.join("", flat));
            });
            merged.add(row);
        }
        return merged;
    }

    private List<TimetableEntry> normalize(List<Map<String, String>> raw) {
        List<TimetableEntry> out = new ArrayList<>();
        for (Map<String, String> r : raw) {
            TimetableEntry te = new TimetableEntry();
            te.setTimeSlot(r.get("时间段"));
            te.setSection(r.get("节次"));
            Map<String, List<Course>> dayMap = new LinkedHashMap<>();
            for (String day : WEEKDAYS) {
                String cell = r.get(day);
                if (cell == null || cell.isEmpty()) dayMap.put(day, new ArrayList<>());
                else {
                    List<Course> list = Arrays.stream(cell.split("\\n")).filter(ln -> !ln.trim().isEmpty()).map(this::parseCourseLine).collect(Collectors.toList());
                    dayMap.put(day, list);
                }
            }
            te.setCourses(dayMap);
            out.add(te);
        }
        return out;
    }

    private Course parseCourseLine(String line) {
        Course c = new Course();
        String txt = cleanText(line);
        String[] parts = txt.split("[／\\/]");
        c.setCourseName(parts.length > 0 ? parts[0] : "");
        if (parts.length > 1) c.setSlot(parts[1]);
        if (parts.length > 2) c.setWeeks(parts[2]);
        Map<String, String> extras = new LinkedHashMap<>();
        for (int i = 3; i < parts.length; i++) {
            String[] kv = parts[i].split("[:：]", 2);
            if (kv.length == 2) extras.put(kv[0].trim(), kv[1].trim());
        }
        c.setExtras(extras);
        return c;
    }

    private String cleanText(String text) {
        String t = text.replace('：', ':').replace("*", "");
        t = BLANKS.matcher(t).replaceAll("");
        t = t.replaceAll("\\((\\d+-\\d+节)\\)(?=\\d+-\\d+周)", "/$1/");
        t = t.replaceAll("\\((\\d+-\\d+节)\\)(\\d+周)", "/$1/$2");
        t = t.replaceAll("(学分:\\d+(?:\\.\\d+)?)(?!\\n)", "$1\\n");
        return t.trim();
    }
}
