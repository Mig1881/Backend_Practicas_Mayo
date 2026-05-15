package com.svalero.apicozybites.service;


import com.svalero.apicozybites.domain.Item;
import com.svalero.apicozybites.domain.dto.ItemInDto;
import com.svalero.apicozybites.domain.dto.ItemOutDto;
import com.svalero.apicozybites.exception.ItemNotFoundException;
import com.svalero.apicozybites.repository.ItemRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<ItemOutDto> getAll(String name, String description) {
        List<Item> itemList;

        if (name.isEmpty() && description.isEmpty()) {
            itemList = itemRepository.findAll();
        } else if (name.isEmpty()) {
            itemList = itemRepository.findByDescription(description);
        } else if (description.isEmpty()) {
            itemList = itemRepository.findByName(name);
        } else {
            itemList = itemRepository.findByNameAndDescription(name, description);
        }

        return itemList.stream()
                .map(this::mapToOutDto)
                .toList();
    }

    // Devuelve la entidad real de la BBDD para poder acceder a la imagen en el endpoint específico
    // Para uso interno, entidad completa, incluida la imagen byte[]
    public Item get(long id) throws ItemNotFoundException {
        return itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    // Devuelve el DTO con la URL de la imagen para el endpoint general
    // Para responder al cliente, sin byte[], con imageUrl
    public ItemOutDto getDto(long id) throws ItemNotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        return mapToOutDto(item);
    }


    // --- POST NUEVO que permite subida CON IMAGEN
    public ItemOutDto addWithImage(ItemInDto itemInDto) {
        Item item = modelMapper.map(itemInDto, Item.class);

        // Si hay imagen en el DTO, la agrego
        if (itemInDto.getImage() != null) {
            item.setImage(itemInDto.getImage());  // Aquí guardo la imagen en el producto
        }

        Item newItem = itemRepository.save(item);
        return mapToOutDto(newItem);
    }

    public ItemOutDto modify(long itemId, ItemInDto itemInDto) throws ItemNotFoundException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        byte[] currentImage = item.getImage();

        modelMapper.map(itemInDto, item);

        if (itemInDto.getImage() == null) { // Si no se envía nueva imagen, mantengo la actual
            item.setImage(currentImage);
        } else {
            item.setImage(itemInDto.getImage()); // Si se envía nueva imagen, la actualizo
        }

        Item updatedItem = itemRepository.save(item);

        return mapToOutDto(updatedItem);
    }

    public void remove(long menuItemId) throws ItemNotFoundException {
        itemRepository.findById(menuItemId).orElseThrow(ItemNotFoundException::new);
        itemRepository.deleteById(menuItemId);
    }

    // Metodo auxiliar para convertir Item a ItemOutDto, incluyendo la URL de la imagen
    private ItemOutDto mapToOutDto(Item item) {
        return new ItemOutDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getIsNew(),
                item.getReleaseDate(),
                "/items/" + item.getId() + "/image"
        );
    }
}