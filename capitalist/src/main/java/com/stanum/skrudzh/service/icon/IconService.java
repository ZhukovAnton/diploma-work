package com.stanum.skrudzh.service.icon;

import com.stanum.skrudzh.jpa.model.IconEntity;
import com.stanum.skrudzh.jpa.repository.IconsRepository;
import com.stanum.skrudzh.model.dto.Icon;
import com.stanum.skrudzh.model.dto.Icons;
import com.stanum.skrudzh.model.enums.IconCategoryEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IconService {
    private final IconsRepository iconsRepository;

    public List<IconEntity> indexIcons(String category) {
        List<IconEntity> icons;
        if (category != null) {
            icons = iconsRepository.findByCategory(IconCategoryEnum.valueOf(category));
            if (icons.size() == 0) {
                icons = iconsRepository.findAll();
            }
        } else {
            icons = iconsRepository.findAll();
        }
        return icons;
    }

    public Icons createIconsResponse(List<IconEntity> iconEntities) {
        return new Icons(iconEntities.stream().map(Icon::new).collect(Collectors.toList()));
    }

}
