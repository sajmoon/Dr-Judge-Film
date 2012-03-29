require "twitter"
require_relative "./reviews"
require_relative "./review"
require 'factory_girl'

class TwitterParser

  def initialize()
    @reviews = Reviews.new
  end

  def getTweetsFromMovies(movies)
    movies.each do |movie|
      movieReviews = getTweetsForOneMovie(movie)
      @reviews.merge( movieReviews )
    end
    @reviews 
  end

  def getTweetsForOneMovie(movie)
    puts "one movie"
    Twitter.search(movie.title, :lang => "en").map do |status|
      movieReview = Review.new(:id => status.id, :text => status.text, :source => "twitter", :moviename => movie.title)
      @reviews.add(movieReview)
    end

    @reviews
  end

end
