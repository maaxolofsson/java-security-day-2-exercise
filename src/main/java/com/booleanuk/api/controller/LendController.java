package com.booleanuk.api.controller;

import com.booleanuk.api.model.DTO.LendDTO;
import com.booleanuk.api.model.Item;
import com.booleanuk.api.model.Lend;
import com.booleanuk.api.model.User;
import com.booleanuk.api.payload.response.ErrorResponse;
import com.booleanuk.api.payload.response.LendListResponse;
import com.booleanuk.api.payload.response.MessageResponse;
import com.booleanuk.api.payload.response.Response;
import com.booleanuk.api.repository.ItemRepository;
import com.booleanuk.api.repository.LendRepository;
import com.booleanuk.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("lend")
public class LendController {

    @Autowired
    private ItemRepository items;

    @Autowired
    private UserRepository users;

    @Autowired
    private LendRepository lends;

    @PostMapping
    public ResponseEntity<Response<?>> newLend(@RequestBody LendDTO request) {
        User user = this.users.findById(request.getUserId().getId()).orElse(null);

        Item item = this.items.findById(request.getItemId().getId()).orElse(null);

        if (user == null || item == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        if(item.isOccupied()){
            ErrorResponse errorResponse = new ErrorResponse("item is occupied");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        item.setOccupied(true);
        Lend lend = new Lend(request.getUserId(), request.getItemId(), true);

        this.lends.save(lend);
        user.addLend(lend);
        item.addLend(lend);

        MessageResponse lendResponse = new MessageResponse("successfully added lend");

        return new ResponseEntity<>(lendResponse, HttpStatus.CREATED);
    }

    @PostMapping("/return")
    public ResponseEntity<Response<?>> returnLend(@RequestBody LendDTO request) {
        User user = this.users.findById(request.getUserId().getId()).orElse(null);

        Item item = this.items.findById(request.getItemId().getId()).orElse(null);

        if (user == null || item == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }


        item.setOccupied(false);
        this.items.save(item);

        Lend lend = this.lends.findByUserIdAndItemId(user.getId(), item.getId());

        if (lend == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        lend.setActive(false);
        this.lends.save(lend);

        MessageResponse lendResponse = new MessageResponse("successfully returned lend");
        return new ResponseEntity<>(lendResponse, HttpStatus.CREATED);
    }

    @GetMapping("{userId}/active")
    public ResponseEntity<Response<?>> getActiveLends(@PathVariable int userId) {
        User user = this.users.findById(userId).orElse(null);

        if (user == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<Lend> toReturn = this.lends.findAllByActiveTrueAndUserId(userId);

        LendListResponse lendListResponse = new LendListResponse();
        lendListResponse.set(toReturn);

        return new ResponseEntity<>(lendListResponse, HttpStatus.OK);
    }

    @GetMapping("{userId}/historic")
    public ResponseEntity<Response<?>> getHistoricLends(@PathVariable int userId) {
        User user = this.users.findById(userId).orElse(null);

        if (user == null) {
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<Lend> toReturn = this.lends.findAllByActiveFalseAndUserId(userId);

        LendListResponse lendListResponse = new LendListResponse();
        lendListResponse.set(toReturn);

        return new ResponseEntity<>(lendListResponse, HttpStatus.OK);
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<Response<?>> getLendsByUser(@PathVariable int userId){
        User user = this.users.findById(userId).orElse(null);

        if(user == null){
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<Lend> toReturn = this.lends.findAllByUserId(userId);

        LendListResponse lendListResponse = new LendListResponse();
        lendListResponse.set(toReturn);
        return new ResponseEntity<>(lendListResponse, HttpStatus.OK);
    }

    @GetMapping("item/{itemId}") // This should be accessible only to admins
    public ResponseEntity<Response<?>> getLendsByItem(@PathVariable int itemId){
        Item item = this.items.findById(itemId).orElse(null);

        if(item == null){
            ErrorResponse errorResponse = new ErrorResponse("not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<Lend> toReturn = this.lends.findAllByItemId(itemId);

        LendListResponse lendListResponse = new LendListResponse();
        lendListResponse.set(toReturn);
        return new ResponseEntity<>(lendListResponse, HttpStatus.OK);
    }

}
