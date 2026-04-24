package com.no_broker_application.Web_UI.controller;

import com.no_broker_application.Web_UI.client.PropertyServiceClient;
import com.no_broker_application.Web_UI.dto.*;
import com.no_broker_application.Web_UI.security.CurrentUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyServiceClient propertyServiceClient;
    private final ObjectMapper objectMapper;
    private final CurrentUserService currentUserService;

    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome";
    }

    @GetMapping("/")
    public String getForm1(Model model) {
        model.addAttribute("property", new Property());
        model.addAttribute("editMode", false);
        return "property-details";
    }

    @PostMapping("/propertyDetails")
    public String addPropertyDetails(Property property, HttpSession session) {
        session.setAttribute("property", property);
        return "redirect:/localityDetails";
    }

    @GetMapping("/localityDetails")
    public String getForm2(HttpSession session, Model model) {
        Property property = (Property) session.getAttribute("property");
        if (property == null)
            return "redirect:/";
        Address address = property.getAddress();
        if (address == null)
            address = new Address();
        model.addAttribute("address", address);
        model.addAttribute("editMode", false);
        return "locality-details";
    }

    @PostMapping("/localityDetails")
    public String addAddress(@ModelAttribute Address address, HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        if (property == null)
            return "redirect:/";
        property.setAddress(address);
        session.setAttribute("property", property);
        return "redirect:/rentalDetails";
    }

    @GetMapping("/rentalDetails")
    public String getForm3(HttpSession session, Model model) {
        Property property = (Property) session.getAttribute("property");
        if (property == null)
            return "redirect:/";
        RentalDto rentalDto = new RentalDto();
        rentalDto.setIsSale(property.getIsSale());
        rentalDto.setAvailableFor(property.getAvailableFor());
        rentalDto.setAvailableFrom(property.getAvailableFrom());
        rentalDto.setExpectedRent(property.getExpectedRent() != null ? property.getExpectedRent() : 0L);
        rentalDto.setExpectedDeposit(property.getExpectedDeposit() != null ? property.getExpectedDeposit() : 0L);
        rentalDto.setNegotiation(property.getNegotiation());
        rentalDto.setMonthlyMaintenance(property.getMonthlyMaintenance());
        rentalDto.setPreferredTenets(property.getPreferredTenets());
        rentalDto.setFurnishing(property.getFurnishing());
        rentalDto.setParking(property.getParking());
        rentalDto.setDescription(property.getDescription());
        rentalDto.setPrice(property.getPrice());
        rentalDto.setPropertyStatus(property.getPropertyStatus());
        model.addAttribute("property", property);
        model.addAttribute("rentalDto", rentalDto);
        model.addAttribute("editMode", false);
        return "rental-details";
    }

    @PostMapping("/rentalDetails")
    public String addRentalDetails(@ModelAttribute RentalDto rentalDto, HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        if (property == null)
            return "redirect:/";
        property.setIsSale(rentalDto.getIsSale());
        property.setAvailableFor(rentalDto.getAvailableFor());
        property.setAvailableFrom(rentalDto.getAvailableFrom());
        property.setExpectedRent(rentalDto.getExpectedRent());
        property.setExpectedDeposit(rentalDto.getExpectedDeposit() != null ? rentalDto.getExpectedDeposit() : 0L);
        property.setNegotiation(rentalDto.getNegotiation());
        property.setMonthlyMaintenance(rentalDto.getMonthlyMaintenance());
        property.setPreferredTenets(rentalDto.getPreferredTenets());
        property.setFurnishing(rentalDto.getFurnishing());
        property.setParking(rentalDto.getParking());
        property.setDescription(rentalDto.getDescription());
        property.setPrice(rentalDto.getPrice());
        property.setPropertyStatus(rentalDto.getPropertyStatus());
        session.setAttribute("property", property);
        return "redirect:/amenities";
    }

    @GetMapping("/amenities")
    public String showAmenityForm(HttpSession session, Model model) {
        Property property = (Property) session.getAttribute("property");
        if (property == null)
            return "redirect:/";
        if (property.getAmenity() == null)
            property.setAmenity(new Amenity());
        model.addAttribute("editMode", false);
        model.addAttribute("amenity", property.getAmenity());
        return "amenities-details";
    }

    @PostMapping("/amenities")
    public String addAmenities(@ModelAttribute Amenity amenity, HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        if (property == null)
            return "redirect:/";
        property.setAmenity(amenity);
        session.setAttribute("property", property);
        return "redirect:/images";
    }

    @GetMapping("/images")
    public String propertyImages(Model model) {
        model.addAttribute("editMode", false);
        return "gallery";
    }

    @PostMapping("/images")
    public String uploadImages(@RequestParam("propertyImages") MultipartFile[] propertyImages,
            HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        User user = currentUserService.getCurrentUserOrNull();

        if (property == null) {
            return "redirect:/";
        }
        if (user == null || user.getUserId() == null) {
            return "redirect:/login";
        }

        PropertyRequest request = buildPropertyRequest(property, user.getUserId());

        try {
            String propertyJson = objectMapper.writeValueAsString(request);
            propertyServiceClient.saveProperty(propertyJson, propertyImages);
        } catch (FeignException.Forbidden e) {
            return "redirect:/landingPage";
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize property request", e);
        }
        session.removeAttribute("property");

        return "redirect:/your-properties/" + user.getUserId();
    }

    @GetMapping("/edit/{propertyId}")
    public String showEditForm(@PathVariable Long propertyId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null) {
            return "redirect:/login";
        }
        Property property = propertyServiceClient.getPropertyById(propertyId);
        if (property == null || property.getOwnerId() == null || !property.getOwnerId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You are not allowed to edit this property.");
            return "redirect:/viewProperty/" + propertyId;
        }
        session.setAttribute("property", property);
        model.addAttribute("property", property);
        model.addAttribute("editMode", true);
        return "property-details";
    }

    @PostMapping("/edit/propertyDetails")
    public String updatePropertyDetails(Property updatedProperty, HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        property.setApartmentType(updatedProperty.getApartmentType());
        property.setApartmentName(updatedProperty.getApartmentName());
        property.setBhkType(updatedProperty.getBhkType());
        property.setFloor(updatedProperty.getFloor());
        property.setTotalFloors(updatedProperty.getTotalFloors());
        property.setPropertyAge(updatedProperty.getPropertyAge());
        property.setFacing(updatedProperty.getFacing());
        property.setBuiltUpArea(updatedProperty.getBuiltUpArea());

        session.setAttribute("property", property);
        return "redirect:/edit/address";
    }

    @GetMapping("/edit/address")
    public String showEditAddressForm(HttpSession session, Model model) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        if (property.getAddress() == null) {
            property.setAddress(new Address());
        }

        model.addAttribute("address", property.getAddress());
        model.addAttribute("editMode", true);
        return "locality-details";
    }

    @PostMapping("/edit/address")
    public String updateAddress(@ModelAttribute Address address, HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        property.setAddress(address);
        session.setAttribute("property", property);
        return "redirect:/edit/rentals";
    }

    @GetMapping("/edit/rentals")
    public String showRentalEditForm(HttpSession session, Model model) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        RentalDto rentalDto = new RentalDto();
        rentalDto.setIsSale(property.getIsSale());
        rentalDto.setAvailableFor(property.getAvailableFor());
        rentalDto.setAvailableFrom(property.getAvailableFrom());
        rentalDto.setExpectedRent(property.getExpectedRent() != null ? property.getExpectedRent() : 0L);
        rentalDto.setExpectedDeposit(property.getExpectedDeposit() != null ? property.getExpectedDeposit() : 0L);
        rentalDto.setNegotiation(property.getNegotiation());
        rentalDto.setMonthlyMaintenance(property.getMonthlyMaintenance());
        rentalDto.setPreferredTenets(property.getPreferredTenets());
        rentalDto.setFurnishing(property.getFurnishing());
        rentalDto.setParking(property.getParking());
        rentalDto.setDescription(property.getDescription());
        rentalDto.setPrice(property.getPrice());
        rentalDto.setPropertyStatus(property.getPropertyStatus());

        model.addAttribute("rentalDto", rentalDto);
        model.addAttribute("editMode", true);
        return "rental-details";
    }

    @PostMapping("/edit/rentals")
    public String updateRentalDetails(@ModelAttribute RentalDto rentalDto, HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        property.setIsSale(rentalDto.getIsSale());
        property.setAvailableFor(rentalDto.getAvailableFor());
        property.setAvailableFrom(rentalDto.getAvailableFrom());
        property.setExpectedRent(rentalDto.getExpectedRent());
        property.setExpectedDeposit(rentalDto.getExpectedDeposit());
        property.setNegotiation(rentalDto.getNegotiation());
        property.setMonthlyMaintenance(rentalDto.getMonthlyMaintenance());
        property.setPreferredTenets(rentalDto.getPreferredTenets());
        property.setFurnishing(rentalDto.getFurnishing());
        property.setParking(rentalDto.getParking());
        property.setDescription(rentalDto.getDescription());
        property.setPrice(rentalDto.getPrice());
        property.setPropertyStatus(rentalDto.getPropertyStatus());

        session.setAttribute("property", property);
        return "redirect:/edit/amenities";
    }

    @GetMapping("/edit/amenities")
    public String showEditAmenityForm(HttpSession session, Model model) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        if (property.getAmenity() == null) {
            property.setAmenity(new Amenity());
        }

        model.addAttribute("amenity", property.getAmenity());
        model.addAttribute("editMode", true);
        return "amenities-details";
    }

    @PostMapping("/edit/amenities")
    public String updateAmenities(@ModelAttribute Amenity amenity, HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        property.setAmenity(amenity);
        session.setAttribute("property", property);
        return "redirect:/edit/gallery";
    }

    @GetMapping("/edit/gallery")
    public String showGalleryEditForm(HttpSession session, Model model) {
        Property property = (Property) session.getAttribute("property");
        if (property == null) return "redirect:/";
        if (!isOwner(property)) return "redirect:/landingPage";

        model.addAttribute("property", property);
        model.addAttribute("editMode", true);
        return "gallery";
    }

    @GetMapping("/edit/gallery/{propertyId}")
    public String showGalleryEditFormById(@PathVariable Long propertyId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null) {
            return "redirect:/login";
        }
        Property property = propertyServiceClient.getPropertyById(propertyId);
        if (property == null || property.getOwnerId() == null || !property.getOwnerId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You are not allowed to edit this property.");
            return "redirect:/viewProperty/" + propertyId;
        }
        session.setAttribute("property", property);
        model.addAttribute("property", property);
        model.addAttribute("editMode", true);
        return "gallery";
    }

    @PostMapping("/edit/gallery")
    public String updateGallery(@RequestParam("propertyImages") MultipartFile[] propertyImages,
                                HttpSession session) {
        Property property = (Property) session.getAttribute("property");
        User user = currentUserService.getCurrentUserOrNull();

        if (property == null) return "redirect:/";
        if (user == null || user.getUserId() == null) return "redirect:/login";
        if (!isOwner(property)) return "redirect:/landingPage";

        PropertyRequest request = buildPropertyRequest(property, user.getUserId());
        request.setPropertyId(property.getPropertyId());

        try {
            String propertyJson = objectMapper.writeValueAsString(request);
            propertyServiceClient.updateProperty(property.getPropertyId(), propertyJson, propertyImages);
        } catch (FeignException.Forbidden e) {
            return "redirect:/viewProperty/" + property.getPropertyId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize property request", e);
        }

        session.removeAttribute("property");
        return "redirect:/your-properties/" + user.getUserId();
    }

    private boolean isOwner(Property property) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null) return false;
        if (property == null || property.getOwnerId() == null) return false;
        return user.getUserId().equals(property.getOwnerId());
    }

    private PropertyRequest buildPropertyRequest(Property property, Long ownerId) {
        PropertyRequest request = new PropertyRequest();
        request.setPropertyId(property.getPropertyId());
        request.setApartmentType(property.getApartmentType());
        request.setApartmentName(property.getApartmentName());
        request.setBhkType(property.getBhkType() != null ? property.getBhkType().longValue() : null);
        request.setFloor(property.getFloor() != null ? property.getFloor().longValue() : null);
        request.setTotalFloors(property.getTotalFloors() != null ? property.getTotalFloors().longValue() : null);
        request.setPropertyAge(property.getPropertyAge() != null ? property.getPropertyAge().longValue() : null);
        request.setFacing(property.getFacing());
        request.setBuiltUpArea(property.getBuiltUpArea());
        request.setAvailableFor(property.getAvailableFor());
        request.setExpectedRent(property.getExpectedRent());
        request.setExpectedDeposit(property.getExpectedDeposit());
        request.setMonthlyMaintenance(property.getMonthlyMaintenance());
        request.setPreferredTenets(property.getPreferredTenets());
        request.setNegotiation(property.getNegotiation());
        request.setAvailableFrom(property.getAvailableFrom());
        request.setFurnishing(property.getFurnishing());
        request.setParking(property.getParking());
        request.setPropertyStatus(property.getPropertyStatus());
        request.setPrice(property.getPrice());
        request.setIsSale(property.getIsSale());
        request.setDescription(property.getDescription());
        request.setOwnerId(ownerId);

        if (property.getAddress() != null) {
            request.setCity(property.getAddress().getCity());
            request.setLocality(property.getAddress().getLocality());
            request.setLandmark(property.getAddress().getLandmark());
            request.setLatitude(property.getAddress().getLatitude());
            request.setLongitude(property.getAddress().getLongitude());
        }

        if (property.getAmenity() != null) {
            request.setBathrooms(property.getAmenity().getBathrooms());
            request.setBalcony(property.getAmenity().getBalcony());
            request.setWaterSupply(property.getAmenity().getWaterSupply());
            request.setPetAllowed(property.getAmenity().getPetAllowed());
            request.setGym(property.getAmenity().getGym());
            request.setNonVeg(property.getAmenity().getNonVeg());
            request.setGatedSecurity(property.getAmenity().getGatedSecurity());
            request.setShowProperty(property.getAmenity().getShowProperty());
            request.setPropertyCondition(property.getAmenity().getPropertyCondition());
            request.setSecondaryNumber(property.getAmenity().getSecondaryNumber());
            request.setNearByPlace(property.getAmenity().getNearByPlace());
            request.setLift(property.getAmenity().getLift());
            request.setGasPipeLine(property.getAmenity().getGasPipeLine());
            request.setAirConditioner(property.getAmenity().getAirConditioner());
            request.setPark(property.getAmenity().getPark());
            request.setHouseKeeping(property.getAmenity().getHouseKeeping());
            request.setInternetService(property.getAmenity().getInternetService());
            request.setPowerBackUp(property.getAmenity().getPowerBackUp());
            request.setServentRoom(property.getAmenity().getServentRoom());
            request.setSwimmingPool(property.getAmenity().getSwimmingPool());
            request.setFireSafety(property.getAmenity().getFireSafety());
        }

        return request;
    }

    @GetMapping("/landingPage")
    public String getLandingPage(Model model) {
        return "landing-page";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "custom-login";
    }

    @GetMapping("/getProperties")
    public String getPropertyDetails(
            @RequestParam("type") String isSaleStr,
            @RequestParam("city") String city,
            @RequestParam("query") String searchKeyword,
            @RequestParam(required = false) List<Integer> bhkType,
            @RequestParam(required = false) String propertyStatus,
            @RequestParam(required = false) List<String> furnishing,
            @RequestParam(required = false) List<String> propertyType,
            @RequestParam(required = false) List<String> parking,
            @RequestParam(required = false) Integer propertyAge,
            @RequestParam(required = false) Double minBuiltUpArea,
            @RequestParam(required = false) Double maxBuiltUpArea,
            @RequestParam(required = false, defaultValue = "0") Long minRent,
            @RequestParam(required = false, defaultValue = "1000000000") Long maxRent,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model, HttpSession session) {

        boolean isSale = isSaleStr.equalsIgnoreCase("true") || isSaleStr.equals("1")
                || isSaleStr.equalsIgnoreCase("buy");

        furnishing = normalizeFurnishing(furnishing);
        parking = normalizeParking(parking);

        User user = currentUserService.getCurrentUserOrNull();
        if (user != null && user.getUserId() != null) {
            try {
                List<Long> bookmarkedIds = propertyServiceClient.getBookmarkedPropertyIds(user.getUserId());
                model.addAttribute("bookmarkedProperties", bookmarkedIds);
            } catch (Exception ignored) {
                model.addAttribute("bookmarkedProperties", List.of());
            }
        }

        try {
            PageResponse<Property> result = propertyServiceClient.searchProperties(
                    isSale, city, searchKeyword, bhkType, propertyStatus,
                    furnishing, propertyType, parking, propertyAge,
                    minBuiltUpArea, maxBuiltUpArea, minRent, maxRent,
                    sortBy, page, size);

            model.addAttribute("allProperties", result.getContent());
            model.addAttribute("currentPage", result.getNumber());
            model.addAttribute("totalPages", result.getTotalPages());
        } catch (Exception e) {
            model.addAttribute("allProperties", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("error", "Could not load properties: " + e.getMessage());
        }

        model.addAttribute("isSale", isSale);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("searchQuery", searchKeyword);
        model.addAttribute("city", city);
        model.addAttribute("bhkType", bhkType);
        model.addAttribute("propertyStatus", propertyStatus);
        model.addAttribute("furnishing", furnishing);
        model.addAttribute("propertyType", propertyType);
        model.addAttribute("parking", parking);
        model.addAttribute("propertyAge", propertyAge);
        model.addAttribute("minBuiltUpArea", minBuiltUpArea);
        model.addAttribute("maxBuiltUpArea", maxBuiltUpArea);
        model.addAttribute("minRent", minRent);
        model.addAttribute("maxRent", maxRent);
        model.addAttribute("sortBy", sortBy);
        return "all-properties";
    }

    @GetMapping("/viewProperty/{id}")
    public String viewProperty(@PathVariable Long id, Model model) {
        try {
            Property property = propertyServiceClient.getPropertyById(id);
            model.addAttribute("property", property);
        } catch (Exception e) {
            model.addAttribute("error", "Property not found");
        }
        return "view-full-property";
    }

    @PostMapping("/property/{propertyId}")
    public String deleteProperty(@PathVariable Long propertyId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            propertyServiceClient.deleteProperty(propertyId);
            redirectAttributes.addFlashAttribute("success", "Property deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not delete property");
        }
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null) {
            return "redirect:/landingPage";
        }
        return "redirect:/your-properties/" + user.getUserId();
    }

    @GetMapping("/loadMorePosts")
    public String loadMorePosts(
            @RequestParam("type") String isSaleStr,
            @RequestParam("city") String city,
            @RequestParam("query") String searchKeyword,
            @RequestParam(required = false) List<Integer> bhkType,
            @RequestParam(required = false) String propertyStatus,
            @RequestParam(required = false) List<String> furnishing,
            @RequestParam(required = false) List<String> propertyType,
            @RequestParam(required = false) List<String> parking,
            @RequestParam(required = false) Integer propertyAge,
            @RequestParam(required = false) Double minBuiltUpArea,
            @RequestParam(required = false) Double maxBuiltUpArea,
            @RequestParam(required = false, defaultValue = "0") Long minRent,
            @RequestParam(required = false, defaultValue = "1000000000") Long maxRent,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model,
            HttpSession session) {
        boolean isSale = isSaleStr.equalsIgnoreCase("true") || isSaleStr.equals("1")
                || isSaleStr.equalsIgnoreCase("buy");

        furnishing = normalizeFurnishing(furnishing);
        parking = normalizeParking(parking);

        User user = currentUserService.getCurrentUserOrNull();
        if (user != null && user.getUserId() != null) {
            try {
                List<Long> bookmarkedIds = propertyServiceClient.getBookmarkedPropertyIds(user.getUserId());
                model.addAttribute("bookmarkedProperties", bookmarkedIds);
            } catch (Exception ignored) {
                model.addAttribute("bookmarkedProperties", List.of());
            }
        }

        try {
            PageResponse<Property> result = propertyServiceClient.searchProperties(
                    isSale, city, searchKeyword, bhkType, propertyStatus,
                    furnishing, propertyType, parking, propertyAge,
                    minBuiltUpArea, maxBuiltUpArea, minRent, maxRent,
                    sortBy, page, size);

            model.addAttribute("allProperties", result.getContent());
            model.addAttribute("hasNext", result.getTotalPages() > result.getNumber() + 1);
        } catch (Exception e) {
            model.addAttribute("allProperties", List.of());
            model.addAttribute("hasNext", false);
        }

        return "fragments/postSection :: postSection";
    }

    @PostMapping("/toggleBookmark/{propertyId}")
    @ResponseBody
    public Map<String, Object> toggleBookmark(@PathVariable Long propertyId, HttpSession session) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null) {
            return Map.of("success", false, "message", "Not logged in");
        }

        return propertyServiceClient.toggleBookmark(propertyId, user.getUserId());
    }

    @PostMapping("/removeBookMarks/{userId}/{propertyId}")
    public String removeBookMarks(@PathVariable Long userId, @PathVariable Long propertyId) {
        User current = currentUserService.getCurrentUserOrNull();
        if (current == null || current.getUserId() == null || !current.getUserId().equals(userId)) {
            return "redirect:/landingPage";
        }
        try {
            propertyServiceClient.toggleBookmark(propertyId, userId);
        } catch (Exception e) {
            return "redirect:/shortlisted-properties/" + userId + "?bookmarkError=true";
        }
        return "redirect:/shortlisted-properties/" + userId;
    }

    private static List<String> normalizeFurnishing(List<String> furnishing) {
        if (furnishing == null || furnishing.isEmpty()) return furnishing;
        return furnishing.stream()
                .filter(v -> v != null && !v.isBlank())
                .flatMap(v -> switch (v) {
                    case "Fully-Furnished", "Fully Furnished" ->
                            java.util.stream.Stream.of("Fully Furnished", "Fully-Furnished");
                    case "Semi-Furnished", "Semi Furnished" ->
                            java.util.stream.Stream.of("Semi Furnished", "Semi-Furnished");
                    default -> java.util.stream.Stream.of(v);
                })
                .distinct()
                .toList();
    }

    private static List<String> normalizeParking(List<String> parking) {
        if (parking == null || parking.isEmpty()) return parking;
        return parking.stream()
                .filter(v -> v != null && !v.isBlank())
                .flatMap(v -> {
                    return switch (v) {
                        case "2-Wheeler" -> java.util.stream.Stream.of("2-Wheeler", "Bike");
                        case "4-Wheeler" -> java.util.stream.Stream.of("4-Wheeler", "Car");
                        case "Bike" -> java.util.stream.Stream.of("Bike", "2-Wheeler");
                        case "Car" -> java.util.stream.Stream.of("Car", "4-Wheeler");
                        case "both", "Both" -> java.util.stream.Stream.of("Both", "both");
                        default -> java.util.stream.Stream.of(v);
                    };
                })
                .distinct()
                .toList();
    }
}
