require 'twitter'
require 'builder'
require 'imdb'
require 'xmlsimple'

puts 'Dr Judge Film'
puts "-------------"
puts "v.2012-03-26"
puts "Flags: -tweets -imdb"
puts ""

require_relative 'movies'
require_relative 'movie'
require_relative 'reviews'
require_relative 'review'
require_relative 'twitter_parser'

tweetFlag = false
imdbFlag = false
tweetsXmlFilename = "tweetData.xml"
movieXmlFilename = "movieData.xml"

ARGV.each do|a|
  if a == "-tweets" then
    tweetFlag = true
  elsif a == "-imdb" then
    imdbFlag = true
  end
end

def getMovieById(m)
  imdbMovie = Imdb::Movie.new(m)
  @movie = Movie.new(
    :title => imdbMovie.title,
    :id => imdbMovie.id,
    :url => imdbMovie.url,
    :rating => imdbMovie.rating,
    :votes => imdbMovie.votes
  )
end

def getMovieByTitle(m)
  i = Imdb::Search.new(m)
  imdbMovie = i.movies.first
    
  @movie = Movie.new(
    :title => imdbMovie.title,
    :id => imdbMovie.id,
    :url => imdbMovie.url,
    :rating => imdbMovie.rating,
    :votes => imdbMovie.votes
  )
end

def getMovieInformation(inputMovies)
  @movies = Movies.new
  inputMovies.each do |m|
    @movie = getMovieByTitle(m)
    @movies.add(@movie)
  end
  @movies
end

def getAllMovieReviewsForAllMovies(movies)
  allReviews = Reviews.new
  movies.each do |movie|
    allReviews.merge(getAllMovieReviews(movie))
  end
  allReviews
end

def getAllMovieReviews(movie)
  @reviews = Reviews.new
  movieReviews = Imdb::MovieReviews.new(movie.id)
  movieReviews.user_reviews(movie.id).each_with_index do |review, index|
    newReview = Review.new(:id => (movie.id.to_s + "_" + index.to_s), :text => review.to_s, :source => "imdb", :moviename => movie.title)

    @reviews.add(newReview)
  end
  @reviews
end

def listOfMovieTitles
  movies = ['Schindlers List', 'Inception', 'Fight Club', 'Goodfellas', 'The Matrix', 'Shawshank Redeption']
end

#if imdbFlag
  puts "-----"
  puts "Get movie information from IMDB"
  movielist = listOfMovieTitles
  @movies = getMovieInformation(movielist)
  #writeToFile(@movies, movieXmlFilename)
#end

if tweetFlag 
  puts "-----"
  puts "To the tweets! "
  @twitterParser = TwitterParser.new
  @reviews = @twitterParser.getTweetsFromMovies(@movies)

  @reviews.saveToDisc()
  #xmlTweets = getTweets(hashtags)
  #writeToFile(xmlTweets,tweetsXmlFilename)
end

puts "-----"
puts "And onwards to the reviews!"

#@movie = getMovieById("1375666")
  @reviews = getAllMovieReviewsForAllMovies(@movies)

@reviews.saveToDisc()

puts "-----"
puts "Done, exiting"

