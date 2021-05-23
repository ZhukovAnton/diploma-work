package com.stanum.skrudzh.service.reminder;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.ReminderRepository;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderEntity findBySourceIdAndType(Long sourceId, RemindableTypeEnum sourceType) {
        return reminderRepository.findFirstByRemindableIdAndRemindableType(sourceId, sourceType.name()).orElse(null);
    }

    public List<ReminderEntity> findRemindersByStartDate(Timestamp startDate) {
        return reminderRepository.findAllByStartDate(startDate);
    }

    public boolean isShouldRemind(ReminderEntity reminderEntity, Object source) {
        boolean shouldToRemind = reminderEntity.getDeletedAt() == null;
        shouldToRemind &= shouldRemind(source);
        return shouldToRemind;
    }

    public void updateStartDateByRecurrenceRule(ReminderEntity reminderEntity) {
        if (reminderEntity.getRecurrenceRule() == null) return;
        String[] parsedRecurrenceRule = reminderEntity.getRecurrenceRule().split("[:\n]");
        if (parsedRecurrenceRule.length > 1) {
            try {
                RecurrenceRule recurrenceRule = new RecurrenceRule(parsedRecurrenceRule[1]);
                if (reminderEntity.getStartDate() != null) {
                    RecurrenceRuleIterator iterator = recurrenceRule
                            .iterator(new DateTime(reminderEntity.getStartDate().getTime()));
                    iterator.nextMillis();
                    if (iterator.hasNext()) {
                        Timestamp nextNotificationDate = new Timestamp(iterator.nextMillis());
                        reminderEntity.setStartDate(nextNotificationDate);
                        save(reminderEntity);
                    }
                }
            } catch (InvalidRecurrenceRuleException exception) {
                log.error(exception.getMessage(), exception);
            }
        }
    }

    public void save(ReminderEntity reminderEntity) {
        reminderRepository.save(reminderEntity);
    }

    public void saveCreatedEntity(Reminder payload, Object reminderSource) {
        if (payload == null) {
            save(createDefaultReminderEntity(reminderSource));
            return;
        }
        if (payload.getId() == null) {
            save(createReminderEntity(payload, reminderSource));
        }
    }

    public void saveUpdatedEntity(Reminder payload, Object reminderSource) {
        save(updateReminderEntity(payload, reminderSource));
    }

    private ReminderEntity updateReminderEntity(Reminder payload, Object reminderSource) {
        Optional<ReminderEntity> reminderEntityOptional = reminderRepository
                .findFirstByIdAndRemindableIdAndRemindableType(
                        payload.getId(),
                        defineId(reminderSource),
                        defineType(reminderSource));
        ReminderEntity reminderEntity = reminderEntityOptional
                .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "Reminder not found"));
        reminderEntity.setMessage(payload.getMessage());
        reminderEntity.setStartDate(payload.getStartDate() != null
                ? Timestamp.valueOf(payload.getStartDate().toLocalDateTime())
                : null);
        if (payload.getRecurrenceRule() != null) {
            reminderEntity.setRecurrenceRule(payload.getRecurrenceRule());
            if (reminderEntity.getStartDate() != null && reminderEntity.getStartDate().before(TimeUtil.now())) {
                updateStartDateByRecurrenceRule(reminderEntity);
            }
        } else {
            reminderEntity.setRecurrenceRule(null);
        }
        reminderEntity.setUpdatedAt(TimeUtil.now());
        setReminderSource(reminderEntity, reminderSource);
        return reminderEntity;
    }

    private ReminderEntity createReminderEntity(Reminder payload, Object reminderSource) {
        ReminderEntity reminderEntity = new ReminderEntity();
        reminderEntity.setMessage(payload.getMessage());
        reminderEntity.setRecurrenceRule(payload.getRecurrenceRule());
        reminderEntity.setStartDate(payload.getStartDate() != null
                ? Timestamp.valueOf(payload.getStartDate().toLocalDateTime())
                : null);
        setCreationTimestamps(reminderEntity);
        setReminderSource(reminderEntity, reminderSource);
        return reminderEntity;
    }

    private ReminderEntity createDefaultReminderEntity(Object reminderSource) {
        ReminderEntity reminderEntity = new ReminderEntity();
        setCreationTimestamps(reminderEntity);
        setReminderSource(reminderEntity, reminderSource);
        return reminderEntity;
    }

    private void setCreationTimestamps(ReminderEntity reminderEntity) {
        Timestamp now = TimeUtil.now();
        reminderEntity.setCreatedAt(now);
        reminderEntity.setUpdatedAt(now);
    }

    private void setReminderSource(ReminderEntity reminderEntity, Object reminderSource) {
        String sourceType = defineType(reminderSource);
        Long sourceId = defineId(reminderSource);
        reminderEntity.setRemindableType(sourceType);
        reminderEntity.setRemindableId(sourceId);
    }

    private String defineType(Object reminderSource) {
        if (reminderSource instanceof IncomeSourceEntity) {
            return RemindableTypeEnum.IncomeSource.name();
        } else if (reminderSource instanceof ExpenseCategoryEntity) {
            return RemindableTypeEnum.ExpenseCategory.name();
        } else if (reminderSource instanceof ActiveEntity) {
            return RemindableTypeEnum.Active.name();
        } else if (reminderSource instanceof CreditEntity) {
            return RemindableTypeEnum.Credit.name();
        } else {
            return RemindableTypeEnum.Borrow.name();
        }
    }

    private Long defineId(Object reminderSource) {
        if (reminderSource instanceof IncomeSourceEntity) {
            return ((IncomeSourceEntity) reminderSource).getId();
        } else if (reminderSource instanceof ExpenseCategoryEntity) {
            return ((ExpenseCategoryEntity) reminderSource).getId();
        } else if (reminderSource instanceof ActiveEntity) {
            return ((ActiveEntity) reminderSource).getId();
        } else if (reminderSource instanceof CreditEntity) {
            return ((CreditEntity) reminderSource).getId();
        } else {
            return ((BorrowEntity) reminderSource).getId();
        }
    }

    private boolean shouldRemind(Object entity) {
        if (entity instanceof IncomeSourceEntity) {
            return ((IncomeSourceEntity) entity).getDeletedAt() == null;
        } else if (entity instanceof ExpenseCategoryEntity) {
            ExpenseCategoryEntity expenseCategoryEntity = (ExpenseCategoryEntity) entity;
            return ((ExpenseCategoryEntity) entity).getDeletedAt() == null
                    && (expenseCategoryEntity.getCreditEntity() == null
                    || expenseCategoryEntity.getCreditEntity() != null
                    && !expenseCategoryEntity.getCreditEntity().getIsPaid());
        } else if (entity instanceof ActiveEntity) {
            return ((ActiveEntity) entity).getDeletedAt() == null;
        } else if (entity instanceof CreditEntity) {
            CreditEntity creditEntity = (CreditEntity) entity;
            return ((CreditEntity) entity).getDeletedAt() == null && !creditEntity.getIsPaid();
        } else {
            BorrowEntity borrowEntity = (BorrowEntity) entity;
            return borrowEntity.getDeletedAt() == null && !borrowEntity.getIsReturned();
        }
    }
}
