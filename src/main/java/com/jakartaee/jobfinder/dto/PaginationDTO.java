package com.jakartaee.jobfinder.dto;

import java.util.List;

/**
 * Data Transfer Object for pagination information.
 * Contains the paginated list of items and metadata about the pagination state.
 *
 * @param <T> the type of items in the paginated list
 */
public class PaginationDTO<T> {

    private List<T> items;
    private int currentPage;
    private int totalPages;
    private int totalItems;
    private int itemsPerPage;
    private boolean hasNextPage;
    private boolean hasPreviousPage;

    public PaginationDTO() {
    }

    public PaginationDTO(List<T> items, int currentPage, int totalItems, int itemsPerPage) {
        this.items = items;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.itemsPerPage = itemsPerPage;
        this.totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        this.hasNextPage = currentPage < totalPages;
        this.hasPreviousPage = currentPage > 1;
    }

    // Getters and Setters
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    /**
     * Get the start index for the current page (1-based for display purposes)
     */
    public int getStartIndex() {
        return (currentPage - 1) * itemsPerPage + 1;
    }

    /**
     * Get the end index for the current page (1-based for display purposes)
     */
    public int getEndIndex() {
        return Math.min(currentPage * itemsPerPage, totalItems);
    }

    /**
     * Check if there are any items to display
     */
    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }
}
