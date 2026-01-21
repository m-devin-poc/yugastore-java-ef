# Product Search Feature - Task List

## Design Phase

### 1. **Database Query Design**
- **Task 1.1:** Design YCQL query for partial title search using `LIKE` operator or token-based search
  - Current repository uses simple queries in `products-microservice/src/main/java/com/yugabyte/app/yugastore/repo/ProductMetadataRepo.java`
  - Need to design query for the `title` field in ProductMetadata (located at `products-microservice/src/main/java/com/yugabyte/app/yugastore/domain/ProductMetadata.java`)
  - Consider ALLOW FILTERING implications for Cassandra queries

### 2. **API Design**
- **Task 2.1:** Design new REST endpoint for search in ProductCatalogController
  - Endpoint pattern: `GET /products-microservice/products/search?query={searchTerm}&limit={limit}&offset={offset}`
  - Reference existing endpoint structure in `products-microservice/src/main/java/com/yugabyte/app/yugastore/controller/ProductCatalogController.java`

- **Task 2.2:** Design corresponding endpoint in API Gateway layer (if needed)

- **Task 2.3:** Design corresponding endpoint in React UI controller
  - Pattern: `GET /products/search?query={searchTerm}&limit={limit}&offset={offset}`
  - Reference existing products endpoint in `react-ui/src/main/java/com/yugabyte/yugastore/ui/controller/CronosProductsController.java`

### 3. **UI/UX Design**
- **Task 3.1:** Design search input component placement in Products component
  - Consider placement above product grid in `react-ui/frontend/src/components/Products/index.js`

- **Task 3.2:** Design search interaction flow (real-time vs. submit button)

- **Task 3.3:** Design loading states and empty search results handling

- **Task 3.4:** Design search input styling to match existing design in `react-ui/frontend/src/components/Products/index.css`

### 4. **Performance Considerations**
- **Task 4.1:** Design debounce strategy for real-time search to reduce API calls

- **Task 4.2:** Evaluate need for search result caching

- **Task 4.3:** Consider pagination for search results

## Implementation Phase

### 5. **Backend - Repository Layer**
- **Task 5.1:** Add search method to ProductMetadataRepo interface
  - Add custom `@Query` annotation for title search
  - Reference existing query pattern in `products-microservice/src/main/java/com/yugabyte/app/yugastore/repo/ProductMetadataRepo.java`

### 6. **Backend - Service Layer**
- **Task 6.1:** Add search method to ProductService interface
  - Method signature: `List<ProductMetadata> searchProductsByTitle(String searchTerm, int limit, int offset)`
  - Reference existing interface in `products-microservice/src/main/java/com/yugabyte/app/yugastore/service/ProductService.java`

- **Task 6.2:** Implement search method in ProductServiceImpl
  - Call repository search method
  - Handle empty/null search terms
  - Reference existing implementation pattern in `products-microservice/src/main/java/com/yugabyte/app/yugastore/service/impl/ProductServiceImpl.java`

### 7. **Backend - Controller Layer (Products Microservice)**
- **Task 7.1:** Add search endpoint to ProductCatalogController
  - Add `@RequestMapping` method for `/products-microservice/products/search`
  - Reference existing endpoint structure in `products-microservice/src/main/java/com/yugabyte/app/yugastore/controller/ProductCatalogController.java`
  - Accept query, limit, and offset parameters

### 8. **Backend - API Gateway Layer (if applicable)**
- **Task 8.1:** Add corresponding endpoint in API Gateway ProductCatalogController

- **Task 8.2:** Add Feign client method for search in ProductCatalogRestClient

### 9. **Backend - React UI Controller**
- **Task 9.1:** Add search endpoint to CronosProductsController
  - Pattern: `GET /products/search`
  - Reference existing structure in `react-ui/src/main/java/com/yugabyte/yugastore/ui/controller/CronosProductsController.java`

- **Task 9.2:** Update DashboardRestConsumer to call API Gateway search endpoint

### 10. **Frontend - Component State Management**
- **Task 10.1:** Add search term state to Products component
  - Add to existing state in `react-ui/frontend/src/components/Products/index.js`

- **Task 10.2:** Create handler for search input changes

- **Task 10.3:** Create handler for search submission/execution

### 11. **Frontend - Search API Integration**
- **Task 11.1:** Create new fetch method for search in Products component
  - Pattern similar to existing fetchProducts in `react-ui/frontend/src/components/Products/index.js`
  - Call new `/products/search` endpoint

- **Task 11.2:** Implement debounce logic for real-time search (if applicable)

### 12. **Frontend - UI Implementation**
- **Task 12.1:** Create search input component
  - Add input field with proper styling
  - Add search icon (using react-materialize Icon)

- **Task 12.2:** Integrate search input into Products component render method
  - Place in products-title section

- **Task 12.3:** Add CSS styling for search input in `react-ui/frontend/src/components/Products/index.css`

- **Task 12.4:** Handle empty search results display

- **Task 12.5:** Add loading indicator during search

### 13. **Testing**
- **Task 13.1:** Unit test repository search method

- **Task 13.2:** Unit test service layer search method

- **Task 13.3:** Integration test for search endpoint

- **Task 13.4:** Frontend component testing for search functionality

- **Task 13.5:** End-to-end testing of complete search flow

### 14. **Documentation & Refinement**
- **Task 14.1:** Update API documentation with new search endpoint

- **Task 14.2:** Add inline code comments

- **Task 14.3:** Performance testing and optimization

- **Task 14.4:** User acceptance testing

## Notes

- The application uses a microservices architecture with React frontend, so changes need to be made across multiple layers
- YugabyteDB YCQL (Cassandra-compatible) is used as the database, which may require `ALLOW FILTERING` for partial text search
- Consider using a search index or full-text search solution for better performance in production
- The Products component currently supports category filtering and pagination, which should be compatible with the new search feature
