package com.example.banknew.controller;

import com.example.banknew.dtos.AccountDto;
import com.example.banknew.dtos.AgreementDto;
import com.example.banknew.dtos.ClientDto;
import com.example.banknew.dtos.ManagerDto;
import com.example.banknew.entities.ClientEntity;
import com.example.banknew.entities.ManagerEntity;
import com.example.banknew.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @Operation(summary = "Получить список всех менеджеров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджеры найдены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ManagerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Менеджеры не найдены",
                    content = @Content)})
    @GetMapping("/")
    public List<ManagerDto> getAll() {
        List<ManagerDto> managers = managerService.getAll();
        return managers;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @Operation(summary = "Получить менеджеров по фамилии")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджеры найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ManagerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Менеджеры не найден",
                    content = @Content)})

    @GetMapping("/search")
    public List<ManagerDto> getByLastName(@Parameter(description = "фамилия менеджера, по которой ведется поиск", example = "Иванов") @RequestParam String lastName) {
        return managerService.findByLastName(lastName);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @Operation(summary = "Получить менеджера по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджер найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ManagerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Менеджер не найден",
                    content = @Content)})
    @GetMapping("/{id}")
    public ManagerDto getById(@Parameter(description = "id менеджера, по которому ведется поиск", example = "2") @PathVariable Long id) {
        return managerService.getById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @Operation(summary = "Создать запись о новом менеджере")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджер создан",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ManagerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Менеджер не создан",
                    content = @Content)})
    @PostMapping("/")
    public ManagerDto add(@RequestBody ManagerDto managerDto) {
        return managerService.createManager(managerDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
    @Operation(summary = "Обновить данные менеджера")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные менеджера обновлены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ManagerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Данные менеджера не обновлены",
                    content = @Content)})
    @PutMapping("/{id}")
    public ManagerDto update(@Parameter(description = "id менеджера, которого надо обновить", example = "2") @PathVariable Long id, @RequestBody ManagerDto managerDto) {
        return managerService.updateManager(id, managerDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Удалить данные о менеджере по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные менеджера удалены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ManagerDto.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Данные менеджера не удалены",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "id менеджера, которого надо удалить", example = "2") @PathVariable Long id) {
        managerService.deleteManager(id);
    }

}


