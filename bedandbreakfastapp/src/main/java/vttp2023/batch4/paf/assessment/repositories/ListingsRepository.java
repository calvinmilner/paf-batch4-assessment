package vttp2023.batch4.paf.assessment.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
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
	 * db.listings.aggregate([
	 * { $match: { "address.country": { $regex: 'australia', $options: 'i'}, "address.suburb": { $nin: ["", null] } }},
	 * { $project: { _id: "$address.suburb" } }
	 * ])
	 *
	 */
	public List<String> getSuburbs(String country) {
		Criteria filterByCountry = Criteria.where("address.country").regex(country, "i")
		Criteria filterBySuburb = Criteria.where("address.suburb").nin("", null);
		MatchOperation matchByCountrySuburb = Aggregation.match(new Criteria().andOperator(filterByCountry, filterBySuburb));
		ProjectionOperation projectFields = Aggregation.project("address.suburb").and("address.suburb").as("_id");
		Aggregation pipeline = Aggregation.newAggregation(filterByCountry, filterBySuburb, matchByCountrySuburb, projectFields);
		List<String> results = template.aggregate(pipeline, "listings", String.class);
		// Criteria criteria = Criteria.where("country").is(country);
		// Query query = Query.query(criteria);
		// List<String> results = template.find(query, String.class, "listings");
		// return results;
		return null;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 * 
	 * db.listings.find({ address.suburb: '$suburb', persons: { $gte: 'accommodates' }, duration: { $lte: 'min_nights' }, priceRange: { $lte: 'price' } })
	 * .projection({_id: 1, name: 1, accommodates: 1, price: 1}).sort(price: -1)
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		// Criteria criteria = new Criteria().andOperator(Criteria.where("suburb").is(suburb), Criteria.where(persons))
		return null;
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
