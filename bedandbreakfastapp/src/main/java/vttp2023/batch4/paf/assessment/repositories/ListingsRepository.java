package vttp2023.batch4.paf.assessment.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {

	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred })
	 * 
	 * ORIGINAL IMPLEMENTATION**
	 * 
	 * db.listings.aggregate([
	 * { $match: { "address.country": { $regex: 'country', $options: 'i'},
	 * "address.suburb": { $nin: ["", null] } }},
	 * { $project: { _id: "$address.suburb" } }
	 * ])
	 *
	 * CLEANER IMPLEMENTATION*****
	 * 
	 * db.listings.aggregate([
	 * { $match: { "address.country": { $regex: 'country', $options: 'i'},
	 * "address.suburb": { $nin: ["", null] } }},
	 * { $group: {_id: "$address.suburb"}}
	 * ]).sort( { _id: 1 } )
	 * 
	 * 
	 */
	public List<String> getSuburbs(String country) {
		Criteria filterByCountry = Criteria.where("address.country").regex(country, "i");
		Criteria filterBySuburb = Criteria.where("address.suburb").nin("", null);

		MatchOperation matchByCountrySuburb = Aggregation
				.match(new Criteria().andOperator(filterByCountry, filterBySuburb));

		GroupOperation groupBySuburb = Aggregation.group("address.suburb");

		SortOperation sortBySuburb = Aggregation.sort(Sort.Direction.ASC, "_id");

		// ProjectionOperation projectFields = Aggregation.project("address.suburb").and("address.suburb").as("_id");

		Aggregation pipeline = Aggregation.newAggregation(matchByCountrySuburb, groupBySuburb, sortBySuburb);

		AggregationResults<Document> results = template.aggregate(pipeline, "listings", Document.class);
		
		List<String> suburbs = results.getMappedResults().stream().map(doc -> doc.getString("_id"))
				.collect(Collectors.toList());
		for (String s : suburbs) {
			System.out.printf(">>>>> %s\n", s);
		}
		return suburbs;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred })
	 * 
	 * db.listings.aggregate([
	 * { 
	 * 	$match: { 
	 * 		"address.suburb": { $regex: 'suburb', $options: 'i' },
	 * 		"accommodates": { $gte: persons },
	 * 		"min_nights": { $lte: duration },
	 * 		"price": { $lte: priceRange }
	 * 	}
	 * },
	 * {
	 * 	$project: { 
	 * 		_id: 1, 
	 * 		name: 1, 
	 * 		accommodates: 1, 
	 * 		price: 1 
	 * 	} 
	 * }
	 * ]).sort({price: -1});
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		Criteria filterBySuburb = Criteria.where("address.suburb").regex(suburb, "i");
		Criteria filterByPersons = Criteria.where("accommodates").gte(persons);
		Criteria filterByDuration = Criteria.where("min_nights").lte(duration);
		Criteria filterByPriceRange = Criteria.where("price").lte(priceRange);

		MatchOperation matchByCriterias = Aggregation.match(new Criteria().andOperator(filterBySuburb, filterByPersons, filterByDuration, filterByPriceRange));

		ProjectionOperation projectFields = Aggregation.project("_id", "name", "accommodates", "price");

		SortOperation sortByPrice = Aggregation.sort(Sort.Direction.DESC, "price");

		Aggregation pipeline = Aggregation.newAggregation(matchByCriterias, projectFields, sortByPrice);

		AggregationResults<Document> results = template.aggregate(pipeline, "listings", Document.class);

		List<AccommodationSummary> as = new LinkedList<>();
		for(Document doc : results) {
			AccommodationSummary s = new AccommodationSummary();
			s.setId(doc.getString("_id"));
			s.setName(doc.getString("name"));
			s.setAccomodates(doc.get("accommodates", Number.class).intValue());
			s.setPrice(doc.get("price", Number.class).floatValue());
			System.out.printf(">>>>> %s\n", doc.toString());
			as.add(s);
			
		}
		return as;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
