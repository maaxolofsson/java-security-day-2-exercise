package com.booleanuk.api.model.DTO;

import com.booleanuk.api.model.Item;
import com.booleanuk.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class LendDTO {
    private User userId;
    private Item itemId;

    public LendDTO(User userId, Item itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    public LendDTO(){

    }
}
