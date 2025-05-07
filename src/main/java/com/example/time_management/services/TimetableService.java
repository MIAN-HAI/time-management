package com.example.time_management.services;

import com.example.time_management.models.Timetable;
import com.example.time_management.dto.Course;
import com.example.time_management.dto.TimetableEntry;
import com.example.time_management.dto.TimetableResponse;
import com.example.time_management.repositories.TimetableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final List<String> WEEKDAYS = Arrays.asList(
        "星期一","星期二","星期三","星期四","星期五","星期六","星期日"
    );
    private static final Pattern BLANKS = Pattern.compile("\\s+");

    @Autowired
    public TimetableService(TimetableRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 解析 PDF 并保存到数据库，返回解析结果
     */
    public TimetableResponse parseAndSave(MultipartFile file, Long userId,String termStart) throws IOException {
        File pdf = toFile(file);
        List<Map<String, String>> raw = extractRaw(pdf);
        List<Map<String, String>> cont = mergeContinuationLines(raw);
        List<Map<String, String>> paged = mergeMultiPage(cont);
        List<TimetableEntry> entries = normalize(paged);

        TimetableResponse resp = new TimetableResponse(termStart, entries);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);

        Timetable entity = new Timetable();
        entity.setUserId(userId);
        entity.setTermStartDate(termStart);
        entity.setJsonData(json);
        // entity.setCreatedAt(LocalDateTime.now());
        repository.save(entity);

        return resp;
    }

    /**
     * 查询某用户所有课程表
     */
    public TimetableResponse getByUserId(Long userId) throws IOException {
        TimetableResponse result = new TimetableResponse();
        List<Timetable> entities = repository.findByUserId(userId);
        Timetable entity = entities.get(0);
        result=mapper.readValue(entity.getJsonData(),TimetableResponse.class);
        result.setTermStartDate(entity.getTermStartDate());
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
                    if(r<10){
                        for (int c = 0; c < header.size(); c++) {
                            map.put(header.get(c), cells.get(c).getText());
                        }
                    }
                    else{
                        for (int c = 0; c < header.size(); c++) {
                            if(c>0) map.put(header.get(c), cells.get(c-1).getText());
                            else map.put(header.get(c), "");
                        }
                    }
                    rows.add(map);
                }
            }
        }
        return rows;
    }



    private List<Map<String, String>> mergeContinuationLines(List<Map<String, String>> data) {
        // 1. 先复制一份，避免修改原 data
        List<Map<String, String>> result = new ArrayList<>(data);
        List<String> cols = new ArrayList<>(data.get(0).keySet());
    
        // 2. 自下而上合并那些节次为空的续行
        for (int i = result.size() - 1; i >= 0; i--) {
            String sec = result.get(i).get("节次");
            if (sec == null || sec.isEmpty()) {
                boolean merged = false;
                for (String col : cols) {
                    if ("时间段".equals(col) || "节次".equals(col)) continue;
                    String txt = result.get(i).get(col);
                    if (txt == null || txt.isEmpty()) continue;
                    // 找到上面最近一行有值的那一行，追加内容
                    for (int j = i - 1; j >= 0; j--) {
                        String prev = result.get(j).get(col);
                        if (prev != null && !prev.isEmpty()) {
                            result.get(j).put(col, prev + "；" + txt);
                            merged = true;
                            break;
                        }
                    }
                }
                if (merged) {
                    result.remove(i);
                }
            }
        }
    
        // 3. 再正序遍历，把空的“时间段”补成上一个非空时间段
        String lastTimeSlot = null;
        for (Map<String, String> row : result) {
            String ts = row.get("时间段");
            if (ts == null || ts.trim().isEmpty()) {
                // 填充
                if (lastTimeSlot != null) {
                    row.put("时间段", lastTimeSlot);
                }
            } else {
                // 更新 lastTimeSlot
                lastTimeSlot = ts.trim();
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
                if (cell == null || cell.trim().isEmpty()) {
                    dayMap.put(day, new ArrayList<>());
                } else {
                    List<Course> list = Arrays.stream(cell.split("\\n"))
                        .filter(ln -> !ln.trim().isEmpty())
                        .map(this::parseCourseLine)
                        .collect(Collectors.toList());
                    dayMap.put(day, list);
                }
            }
            te.setCourses(dayMap);
            out.add(te);
        }
        return out;
    }

    private Course parseCourseLine(String line) {
        // 先做清洗
        String txt = cleanText(line);
        // 按全／半角斜杠拆分：0=课程名,1=节次,2=周次,后面为 key:value
        String[] parts = txt.split("[／\\\\/]");

        Course c = new Course();
        // 0: 课程名
        if (parts.length > 0) c.set课程名(parts[0].trim());
        // 1: 节次
        if (parts.length > 1) c.set节次(parts[1].trim());
        // 2: 周次
        if (parts.length > 2) c.set周次(parts[2].trim());

        // 从第 3 段起，每段形如 “键:值” 或 “键：值”
        for (int i = 3; i < parts.length; i++) {
            String[] kv = parts[i].split("[:：]", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String val = kv[1].trim();

            switch (key) {
                case "校区":           c.set校区(val);           break;
                case "场地":           c.set场地(val);           break;
                case "教师":           c.set教师(val);           break;
                case "教学班":         c.set教学班(val);         break;
                case "教学班组成":     c.set教学班组成(val);     break;
                case "考核方式":       c.set考核方式(val);       break;
                case "选课备注":       c.set选课备注(val);       break;
                case "课程学时组成":   c.set课程学时组成(val);   break;
                case "周学时":         c.set周学时(val);         break;
                case "总学时":         c.set总学时(val);         break;
                case "学分":           c.set学分(val);           break;
                case "科研实践":       c.set科研实践(val);       break;
                // 如果未来有更多字段，再补充 case 即可
                default:
                    // 忽略不认识的字段
            }
        }
        return c;
    }


    private String cleanText(String text) {
        String t = text.replace('：', ':').replace("*", "");
        t = BLANKS.matcher(t).replaceAll("");
        t = t.replaceAll("\\((\\d+-\\d+节)\\)(?=\\d+-\\d+周)", "/$1/");
        t = t.replaceAll("\\((\\d+-\\d+节)\\)(\\d+周)", "/$1/$2");
        t = t.replaceAll("(学分:\\d+(?:\\.\\d+)?)(?!\n)", "$1\n");
        return t.trim();
    }
}
