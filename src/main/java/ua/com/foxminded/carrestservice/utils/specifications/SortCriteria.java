package ua.com.foxminded.carrestservice.utils.specifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SortCriteria {
    private String key;
    private String direction;
}