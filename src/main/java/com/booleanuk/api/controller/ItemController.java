package com.booleanuk.api.controller;

import com.booleanuk.api.model.Item;
import com.booleanuk.api.payload.response.ItemListResponse;
import com.booleanuk.api.payload.response.ItemResponse;
import com.booleanuk.api.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("items")
public class ItemController {

    @Autowired
    private ItemRepository items;

    @GetMapping
    public ResponseEntity<ItemListResponse> getAll() {
        ItemListResponse itemListResponse = new ItemListResponse();
        itemListResponse.set(this.items.findAll());
        return new ResponseEntity<>(itemListResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@RequestBody Item toAdd) {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.set(this.items.save(toAdd));
        return new ResponseEntity<>(itemResponse, HttpStatus.CREATED);
    }

}
