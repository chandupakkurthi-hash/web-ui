package com.no_broker_application.Web_UI.client;

import com.no_broker_application.Web_UI.dto.PageResponse;
import com.no_broker_application.Web_UI.dto.Property;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(name = "property-service")
public interface PropertyServiceClient {

    @GetMapping("/properties/{id}")
    Property getPropertyById(@PathVariable("id") Long id);

    @GetMapping("/properties/owner/{ownerId}")
    List<Property> getPropertiesByOwner(@PathVariable("ownerId") Long ownerId);

    @GetMapping("/properties/search")
    PageResponse<Property> searchProperties(
            @RequestParam("isSale") boolean isSale,
            @RequestParam("city") String city,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "bhkType", required = false) List<Integer> bhkType,
            @RequestParam(value = "propertyStatus", required = false) String propertyStatus,
            @RequestParam(value = "furnishing", required = false) List<String> furnishing,
            @RequestParam(value = "propertyType", required = false) List<String> propertyType,
            @RequestParam(value = "parking", required = false) List<String> parking,
            @RequestParam(value = "propertyAge", required = false) Integer propertyAge,
            @RequestParam(value = "minBuiltUpArea", required = false) Double minBuiltUpArea,
            @RequestParam(value = "maxBuiltUpArea", required = false) Double maxBuiltUpArea,
            @RequestParam(value = "minRent", defaultValue = "0") Long minRent,
            @RequestParam(value = "maxRent", defaultValue = "10000000") Long maxRent,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    );

    @DeleteMapping("/properties/{id}")
    String deleteProperty(@PathVariable("id") Long id);

    @PostMapping(value = "/properties", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Property saveProperty(
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    );

    @PutMapping(value = "/properties/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Property updateProperty(
            @PathVariable("id") Long id,
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    );

    @PostMapping("/bookmarks/toggle/{propertyId}")
    Map<String, Object> toggleBookmark(@PathVariable("propertyId") Long propertyId,
                                       @RequestParam("userId") Long userId);

    @GetMapping("/bookmarks/{userId}/ids")
    List<Long> getBookmarkedPropertyIds(@PathVariable("userId") Long userId);

    @GetMapping("/bookmarks/{userId}")
    List<Property> getBookmarkedProperties(@PathVariable("userId") Long userId);

    @DeleteMapping("/bookmarks/{userId}/{propertyId}")
    Map<String, Object> removeBookmark(@PathVariable("userId") Long userId,
                                       @PathVariable("propertyId") Long propertyId);
}
