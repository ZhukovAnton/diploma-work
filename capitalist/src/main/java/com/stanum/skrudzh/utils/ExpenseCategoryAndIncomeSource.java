package com.stanum.skrudzh.utils;

import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.service.reminder.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryAndIncomeSource {

    private final ReminderService reminderService;

    public void updateEntity(Reminder attributes, Object source) {
        if (attributes != null && attributes.getId() != null) {
            reminderService.saveUpdatedEntity(attributes, source);
        } else {
            reminderService.saveCreatedEntity(attributes, source);
        }
    }
}
