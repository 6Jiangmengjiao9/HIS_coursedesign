package com.liuzhen._02301020417.service.impl;

import com.liuzhen._02301020417.mapper.ConsultingRoomMapper;
import com.liuzhen._02301020417.model.ConsultingRoom;
import com.liuzhen._02301020417.service.ConsultingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultingRoomServiceImpl implements ConsultingRoomService {

    @Autowired
    ConsultingRoomMapper consultingRoomMapper;

    @Override
    public List<ConsultingRoom> getAllConsultingRoom() {
        return consultingRoomMapper.getAllConsultingRoom();
    }

    @Override
    public ConsultingRoom getConsultingRoomById(Integer roomId) {
        return consultingRoomMapper.getConsultingRoomById(roomId);
    }

    @Override
    public List<ConsultingRoom> getConsultingRoomByDepartmentId(String departmentId) {
        return consultingRoomMapper.getConsultingRoomByDepartmentId(departmentId);
    }

    @Transactional
    @Override
    public int addConsultingRoom(ConsultingRoom room) {
        // 默认状态为可用
        if (room.getStatus() == null) {
            room.setStatus(1);
        }
        return consultingRoomMapper.addConsultingRoom(room);
    }

    @Transactional
    @Override
    public int updateConsultingRoom(ConsultingRoom room) {
        return consultingRoomMapper.updateConsultingRoom(room);
    }

    @Transactional
    @Override
    public int deleteConsultingRoom(Integer roomId) {
        return consultingRoomMapper.deleteConsultingRoom(roomId);
    }
}


