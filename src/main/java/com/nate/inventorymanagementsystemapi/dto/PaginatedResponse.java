package com.nate.inventorymanagementsystemapi.dto;

import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class PaginatedResponse<T> {
    private List<T> data;
    private int currentPage;
    private int totalPage;
    private long totalItems;
    private boolean isLast;
}
