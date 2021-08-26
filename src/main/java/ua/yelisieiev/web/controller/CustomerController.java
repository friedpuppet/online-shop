package ua.yelisieiev.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.yelisieiev.entity.Product;
import ua.yelisieiev.service.ProductServiceException;
import ua.yelisieiev.service.ProductsService;

import java.util.List;

@RequestMapping("/")
@Controller()
public class CustomerController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ProductsService productsService;

    public CustomerController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @RequestMapping("/")
    protected String defaultPage() {
        return "redirect:products";
    }

    @RequestMapping("/products")
    protected String getALl(Model model) {
        log.info("Simple getall method entered");
        try {
            List<Product> products;
            products = productsService.getAll();
            model.addAttribute("products", products);
            return "buyable_products";
        } catch (ProductServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error", e);
        }
    }

    @GetMapping(value = "/products", params = "search")
    protected String getSearch(@RequestParam("search") String searchExpression, Model model) {
        log.info("Search method entered");
        try {
            List<Product> products;
            products = productsService.getAllFiltered(searchExpression);
            model.addAttribute("products", products);
            return "buyable_products";
        } catch (ProductServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error", e);
        }
    }
}
