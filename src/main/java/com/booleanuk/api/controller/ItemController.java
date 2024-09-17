package com.booleanuk.api.controller;

import com.booleanuk.api.model.Item;
import com.booleanuk.api.payload.response.ErrorResponse;
import com.booleanuk.api.payload.response.ItemListResponse;
import com.booleanuk.api.payload.response.ItemResponse;
import com.booleanuk.api.payload.response.Response;
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

    @GetMapping("{id}")
    public ResponseEntity<Response<?>> getOne(@PathVariable(name = "id") int id) {
        Item toReturn = this.items.findById(id).orElse(null);

        if (toReturn == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.set(toReturn);

        return new ResponseEntity<>(itemResponse, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@RequestBody Item toAdd) {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.set(this.items.save(toAdd));
        return new ResponseEntity<>(itemResponse, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Response<?>> update(@PathVariable(name = "id") int id, @RequestBody Item newData) {
        Item toUpdate = this.items.findById(id).orElse(null);

        if (toUpdate == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        toUpdate.setName(newData.getName());

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.set(this.items.save(toUpdate));

        return new ResponseEntity<>(itemResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Response<?>> delete(@PathVariable(name = "id") int id) {
        Item toDelete = this.items.findById(id).orElse(null);

        if (toDelete == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.set(toDelete);

        this.items.delete(toDelete);

        return new ResponseEntity<>(itemResponse, HttpStatus.OK);
    }

}
