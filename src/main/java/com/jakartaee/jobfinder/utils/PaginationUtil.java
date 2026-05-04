package com.jakartaee.jobfinder.utils;

import com.jakartaee.jobfinder.dto.PaginationDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for pagination operations.
 */
public class PaginationUtil {

    public static final int DEFAULT_ITEMS_PER_PAGE = 10;
    public static final int MAX_ITEMS_PER_PAGE = 100;

    /**
     * Paginate a list of items.
     *
     * @param items      the full list of items
     * @param page       the requested page number (1-based)
     * @param itemsPerPage number of items per page
     * @return PaginationDTO containing the paginated results
     */
    public static <T> PaginationDTO<T> paginate(List<T> items, int page, int itemsPerPage) {
        if (items == null) {
            return new PaginationDTO<>(List.of(), 1, 0, itemsPerPage);
        }

        // Validate and normalize items per page
        int perPage = Math.max(1, Math.min(itemsPerPage, MAX_ITEMS_PER_PAGE));
        
        // Calculate total pages
        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / perPage);
        
        // Validate and normalize page number
        int currentPage = Math.max(1, Math.min(page, totalPages > 0 ? totalPages : 1));
        
        // Calculate start and end indices
        int startIndex = (currentPage - 1) * perPage;
        int endIndex = Math.min(startIndex + perPage, totalItems);
        
        // Get the sublist for the current page
        List<T> paginatedItems = items.stream()
                .skip(startIndex)
                .limit(perPage)
                .collect(Collectors.toList());
        
        return new PaginationDTO<>(paginatedItems, currentPage, totalItems, perPage);
    }

    /**
     * Paginate with default items per page.
     */
    public static <T> PaginationDTO<T> paginate(List<T> items, int page) {
        return paginate(items, page, DEFAULT_ITEMS_PER_PAGE);
    }

    /**
     * Parse page parameter from request.
     * Returns 1 if the parameter is invalid or missing.
     */
    public static int parsePageParameter(String pageParam) {
        if (pageParam == null || pageParam.isEmpty()) {
            return 1;
        }
        try {
            int page = Integer.parseInt(pageParam);
            return Math.max(1, page);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Parse items per page parameter from request.
     * Returns default if the parameter is invalid or missing.
     */
    public static int parseItemsPerPageParameter(String itemsPerPageParam, int defaultValue) {
        if (itemsPerPageParam == null || itemsPerPageParam.isEmpty()) {
            return defaultValue;
        }
        try {
            int perPage = Integer.parseInt(itemsPerPageParam);
            return Math.max(1, Math.min(perPage, MAX_ITEMS_PER_PAGE));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Generate pagination URL with page parameter.
     */
    public static String buildPageUrl(String baseUrl, int page) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            return "?page=" + page;
        }
        if (baseUrl.contains("?")) {
            // Remove existing page parameter if present
            baseUrl = baseUrl.replaceAll("&?page=\\d+", "");
            return baseUrl + "&page=" + page;
        }
        return baseUrl + "?page=" + page;
    }
}
