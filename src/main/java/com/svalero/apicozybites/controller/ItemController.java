package com.svalero.apicozybites.controller;

import com.svalero.apicozybites.domain.Item;
import com.svalero.apicozybites.domain.dto.ItemInDto;
import com.svalero.apicozybites.domain.dto.ItemOutDto;
import com.svalero.apicozybites.exception.ImageNotFoundException;
import com.svalero.apicozybites.exception.ItemNotFoundException;
import com.svalero.apicozybites.service.ItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;
    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @GetMapping
    public ResponseEntity<List<ItemOutDto>> getAll(@RequestParam(value = "name", defaultValue = "") String name,
                                                   @RequestParam(value = "description", defaultValue = "") String description)  {
        List<ItemOutDto> items = itemService.getAll(name, description);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemOutDto> getItem(@PathVariable long itemId) throws ItemNotFoundException {
        ItemOutDto item = itemService.getDto(itemId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    // Endpoint específico para servir la imagen como archivo binario:
    @GetMapping("/{itemId}/image")
    public ResponseEntity<byte[]> getItemImage(@PathVariable long itemId) throws ItemNotFoundException, ImageNotFoundException {
        Item item = itemService.get(itemId);
        if (item.getImage() == null) {
            throw new ImageNotFoundException();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(item.getImage());
    }

    // Versión que PERMITE SUBIDA DE IMÁGENES
    @PostMapping
    public ResponseEntity<ItemOutDto> addItemWithImage(@RequestBody @Valid ItemInDto item){
        ItemOutDto newItem = itemService.addWithImage(item);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable long itemId) throws ItemNotFoundException {
        itemService.remove(itemId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItemOutDto> modifyItem(@PathVariable  long itemId,@Valid @RequestBody ItemInDto item) throws ItemNotFoundException {
        ItemOutDto modifiedItem = itemService.modify(itemId, item);
        return new ResponseEntity<>(modifiedItem, HttpStatus.OK);
    }
}
