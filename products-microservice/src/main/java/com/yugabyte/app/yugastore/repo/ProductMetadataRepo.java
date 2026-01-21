package com.yugabyte.app.yugastore.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.yugabyte.app.yugastore.domain.ProductMetadata;

@RepositoryRestResource(path = "product")
public interface ProductMetadataRepo extends CassandraRepository<ProductMetadata, String> {
	
	@Query("SELECT * FROM cronos.products limit ?0 offset ?1")
	@RestResource(path = "products", rel = "products")
	public List<ProductMetadata> getProducts(@Param("limit") int limit, @Param("offset") int offset);

	Optional<ProductMetadata> findById(String id);

	/**
	 * Search products by partial title match using YCQL LIKE operator.
	 * 
	 * Design Notes:
	 * - Uses ALLOW FILTERING because the title column is not part of the primary key
	 * - ALLOW FILTERING performs a full table scan, which can be expensive for large datasets
	 * - For production use with large catalogs, consider:
	 *   1. Adding a secondary index on the title column
	 *   2. Using a full-text search solution (e.g., Elasticsearch)
	 *   3. Implementing token-based search with a separate search index table
	 * - The LIKE operator with '%' prefix and suffix enables partial matching anywhere in the title
	 * 
	 * @param searchTerm the search term to match against product titles (will be wrapped with % wildcards)
	 * @param limit maximum number of results to return
	 * @param offset number of results to skip for pagination
	 * @return list of products with titles containing the search term
	 */
	@Query("SELECT * FROM cronos.products WHERE title LIKE ?0 ALLOW FILTERING LIMIT ?1 OFFSET ?2")
	@RestResource(path = "searchByTitle", rel = "searchByTitle")
	public List<ProductMetadata> searchProductsByTitle(
		@Param("searchTerm") String searchTerm,
		@Param("limit") int limit,
		@Param("offset") int offset
	);
}
