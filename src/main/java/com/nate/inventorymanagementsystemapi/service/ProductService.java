package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.exception.ProductNotFoundException;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.mapper.ProductMapper;
import com.nate.inventorymanagementsystemapi.model.Product;
import com.nate.inventorymanagementsystemapi.model.Role;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.repository.ProductRepository;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProductService implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repo;
    private final UserRepository repoU;


    /**
     * Retrieves a Paginated and sorted list of products from the given user
     *
     * @param username the username of the logged in user
     * @param page  the page number that the user wants to retrieve (0-based)
     * @param size  the amount of items per page
     * @param sortBy the field the page is sorted by (e.g name, quantity etc)
     * @param direction the way the pages are sorted (e.g asc or desc)
     * @return a paginated page {@link Page} of {@link ProductDto} objects
     * @throws UserNotFoundException if no user was found with the given username
     */
    @Override
    public Page<ProductDto> getAllUserProductsByUsername(String username, int page, int size, String sortBy, String direction,String search) {
        log.info("Fetching products for user: {}, page {}, size {}, sortBy {}, direction {} ",username,page,size,sortBy,direction);
        //Finds user by username, throws exception if not found
       User user = repoU.findByUsername(username).orElseThrow(()->{
            log.error("User not found: {}",username);
            return new UserNotFoundException(username);
       });

       //Configures sorting (ascending or descending)
       Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

       //Creates a Pageable object that defines page number , size and sorting
        Pageable pageable = PageRequest.of(page,size,sort);

        //Fetches Paginated product data for the given user
        Page<Product> productPage;

        if(user.getRole().equals(Role.ADMIN) && search!= null && !search.trim().isEmpty()){
            productPage = repo.findByNameContainingIgnoreCase(search,pageable);
        } else if (search!=null && !search.trim().isEmpty()) {
            productPage = repo.searchProductByUserAndName(username,search,pageable);
        }
        else {
            productPage = repo.findByUserUsername(username,pageable);
        }

        //if user is Admin, fetches product regardless
        if (user.getRole().equals(Role.ADMIN)) {
            return productPage.map(ProductMapper::toDto);
        }

        //Map Product entities to ProductDto objects using the mapper
        return productPage.map(ProductMapper::toDto);
    }

    /**
     * Add a new Product
     *
     * @param product product thats given by client to Add
     * @param username username of logged in user
     * @return a {@link ProductDto} object
     * @throws UserNotFoundException if user with given username not found
     */
    @Override
    public ProductDto addProduct(PostProduct product, String username) {
        log.info("Adding new product: {}",product.getName());
        //Finds user by username, throws exception if not found
        User user = repoU.findByUsername(username)
                .orElseThrow(()->{
                    log.error("User not found: {}",username);
                    return new UserNotFoundException(username);
                });

        //Creates a ProductDto to store the data retrieved from user
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setQuantity(product.getQuantity());
        dto.setPrice(product.getPrice());

        //Map the ProductDto to Product entity using the mapper
        Product product1 = ProductMapper.toEntity(dto,user);
        //Saves the Product entity to the repo
        Product saved = repo.save(product1);
        log.debug("Saves product id {} to repo",saved.getName());

        //Map the Product entity to ProductDto object using the mapper
        return ProductMapper.toDto(saved);
    }

    /**
     * Retrieves a product with the specified id
     *
     * @param id the id of the specified product
     * @param username the username of the logged in user
     * @return a {@link ProductDto} object of the retrieved product
     * @throws UserNotFoundException if the user with the given username was not found
     * @throws ProductNotFoundException if the product with the given id was not found
     * @throws AccessDeniedException if the user thats trying to retrieve the specified product is not the owner of the product or is not Admin
     */
    @Override
    public ProductDto getProduct(Long id,String username) {
        log.info("Fetching product with id: {}",id);
        //Fetches the user by username, throws exception if not found
        User user = repoU.findByUsername(username)
                .orElseThrow(()->{
                    log.error("User not found: {}",username);
                   return new UserNotFoundException(username);
                });

        //Fetches Product by id , throws exception if not found
        Product product = repo.findById(id)
                .orElseThrow(()-> {
                    log.error("Product not found id: {}",id);
                    return new ProductNotFoundException(id);
                });

        //if User is not owner of product or is not admin, throws exception
        if(!product.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)){
            log.error("Unauthorized access for this product id: {}",id);
            throw new AccessDeniedException("Not Authorized");
        }

        //Map Product entity to ProductDto object using the mapper
        return ProductMapper.toDto(product);
    }

    /**
     * Deletes the product by id
     *
     * @param id the id of the specified product
     * @param username the username of the logged in user
     * @return boolean that indicates whether the item was deleted successfully or not
     * @throws UserNotFoundException if the user with specified username was not found
     * @throws ProductNotFoundException if product with specified id was not found
     * @throws AccessDeniedException if the user thats trying to delete the product is not the owner or is not admin
     */
    @Override
    public boolean deleteProduct(Long id, String username) {
        log.error("Deleting product id: {}",id);
        //Fetches user by username ,throws exception if not found
        User user = repoU.findByUsername(username)
                .orElseThrow(()->{
                    log.error("User not found: {}",username);
                    return new UserNotFoundException(username);
                });

        //Fetches Product by id , throws exception if not found
        Product product = repo.findById(id)
                .orElseThrow(()-> {
                    log.error("Product not found id: {}",id);
                    return new ProductNotFoundException(id);
                });

        //If User is admin, deletes product regardless of ownership
        if (user.getRole().equals(Role.ADMIN)) {
            log.debug("Admin: Found and deleting product id: {}",id);
            repo.delete(product);
            return true;
        }
        //if User is owner, deletes product
        else if (product.getUser().getId().equals(user.getId())) {
            log.debug("User: Found and deleting product id: {}", id);
            repo.delete(product);
            return true;
        }

        //if User is not owner of product or not an admin, throws exception
        log.error("Unauthorized access for product id: {}",id);
        throw new AccessDeniedException("Dont have Authorization");
    }

    /**
     * Updates specified product by id
     *
     * @param id the id of the specified product
     * @param productUpdate the updated {@link ProductDto} object
     * @param username the username of the logged in user
     * @return a updated {@link ProductDto} object
     * @throws UserNotFoundException if the user with the given username is not found
     * @throws ProductNotFoundException if the product with the specified id is not found
     * @throws AccessDeniedException if the user with the specified username is not the owner or is not Admin
     */
    @Override
    public ProductDto udpateProduct(Long id, ProductDto productUpdate,String username) {
        log.error("Updating product id: {}",id);
        //Fetches user by username, throws exception if not found
        User user = repoU.findByUsername(username).orElseThrow(()-> {
            log.error("User not found: {}",username);
            return new UserNotFoundException(username);
        });

        //Fetches product by id , throws exception id if not found
        Product product = repo.findById(id)
                .orElseThrow(()-> {
                    log.error("Product not found id: {}",id);
                    return new ProductNotFoundException(id);
                });

        //if User is not owner of product or not admin, throws exception
        if(!product.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)){
            log.error("Unauthorized access for product id: {}",id);
            throw new AccessDeniedException("Access Denied");
        }

        //Map ProductDto object to Product entity using mapper
        Product updateProd = ProductMapper.updateEntity(product,productUpdate);

        //Saves Product to repo
        log.debug("Found and saving product id: {}",id);
        repo.save(updateProd);

        //Map Product entity to ProductDto Object using mapper
        return ProductMapper.toDto(product);
    }
}
