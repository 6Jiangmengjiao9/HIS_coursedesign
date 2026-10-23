package com.liuzhen._02301020417.mapper;

import com.liuzhen._02301020417.model.ConsultingRoom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConsultingRoomMapper {

    // 查询所有诊室
    List<ConsultingRoom> getAllConsultingRoom();

    // 根据诊室ID查询
    ConsultingRoom getConsultingRoomById(Integer roomId);

    // 根据科室ID查询诊室列表
    List<ConsultingRoom> getConsultingRoomByDepartmentId(String departmentId);

    // 新增诊室
    int addConsultingRoom(ConsultingRoom room);

    // 更新诊室
    int updateConsultingRoom(ConsultingRoom room);

    // 删除诊室
    int deleteConsultingRoom(Integer roomId);
}


