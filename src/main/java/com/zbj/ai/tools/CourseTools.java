package com.zbj.ai.tools;

import com.zbj.ai.entity.po.Course;
import com.zbj.ai.entity.po.CourseReservation;
import com.zbj.ai.entity.po.School;
import com.zbj.ai.entity.query.CourseQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class CourseTools {
    // 无数据库：内存模拟数据
    private static final List<Course> COURSES = List.of(
            new Course().setId(1).setName("Java").setType("编程").setEdu(2).setPrice(3999L).setDuration(45),
            new Course().setId(2).setName("Python").setType("编程").setEdu(1).setPrice(2999L).setDuration(30),
            new Course().setId(3).setName("UI设计").setType("设计").setEdu(0).setPrice(2599L).setDuration(28),
            new Course().setId(4).setName("短视频运营").setType("自媒体").setEdu(0).setPrice(1999L).setDuration(14)
    );
    private static final List<School> SCHOOLS = List.of(
            new School().setId(1).setName("北京中关村校区").setCity("北京"),
            new School().setId(2).setName("上海浦东校区").setCity("上海"),
            new School().setId(3).setName("深圳南山校区").setCity("深圳")
    );
    private static final CopyOnWriteArrayList<CourseReservation> RESERVATIONS = new CopyOnWriteArrayList<>();
    private static final AtomicInteger RESERVATION_ID = new AtomicInteger(1);

    @Tool(description = "根据条件查询课程")
    public List<Course> queryCourse(@ToolParam(description = "查询的条件", required = false) CourseQuery query) {
        Stream<Course> stream = COURSES.stream();
        if (query != null) {
            if (query.getType() != null) {
                stream = stream.filter(course -> course.getType().equals(query.getType()));
            }
            if (query.getEdu() != null) {
                // edu <= query.edu
                stream = stream.filter(c -> c.getEdu() != null && c.getEdu() <= query.getEdu());
            }
            List<Course> result = stream.toList();
            // 排序
            if (query.getSorts() != null && !query.getSorts().isEmpty()) {
                // 简单实现：只支持按price或duration排序
                for (CourseQuery.Sort sort : query.getSorts()) {
                    if ("price".equals(sort.getField())) {
                        if (Boolean.TRUE.equals(sort.getAsc())) {
                            result = result.stream()
                                    .sorted((c1, c2) -> Long.compare(c1.getPrice(), c2.getPrice()))
                                    .toList();
                        } else {
                            result = result.stream()
                                    .sorted((c1, c2) -> Long.compare(c2.getPrice(), c1.getPrice()))
                                    .toList();
                        }
                    } else if ("duration".equals(sort.getField())) {
                        if (Boolean.TRUE.equals(sort.getAsc())) {
                            result = result.stream()
                                    .sorted((c1, c2) -> Integer.compare(c1.getDuration(), c2.getDuration()))
                                    .toList();
                        } else {
                            result = result.stream()
                                    .sorted((c1, c2) -> Integer.compare(c2.getDuration(), c1.getDuration()))
                                    .toList();
                        }
                    }
                }
            }
            return result;
        }
        return stream.toList();
    }

    @Tool(description = "查询所有校区")
    public List<School> querySchool() {
        return  SCHOOLS;
    }

    @Tool(description = "生成预约单，返回预约单号")
    public Integer createCourseReservation(
            @ToolParam(description = "预约课程") String course,
            @ToolParam(description = "预约校区") String school,
            @ToolParam(description = "学生姓名") String studentName,
            @ToolParam(description = "联系电话") String contactInfo,
            @ToolParam(description = "备注", required = false) String remark) {
        CourseReservation reservation = new CourseReservation();
        reservation.setCourse(course);
        reservation.setSchool(school);
        reservation.setStudentName(studentName);
        reservation.setContactInfo(contactInfo);
        reservation.setRemark(remark);
        reservation.setId(RESERVATION_ID.getAndIncrement());
        RESERVATIONS.add(reservation);
        System.out.println("New reservation created: " + RESERVATIONS.stream().toList());
        return reservation.getId();
    }
}
