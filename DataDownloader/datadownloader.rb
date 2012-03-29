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

def getTweets2(hashtags)
  
  hashtags.each do |h|
    Twitter.search(h, :lang => "en").map do |status|

      foldername = h.to_s.delete('#').delete('[').delete(']').delete('"') 
      
      Reviews.new(:id => status.id, :source => status.source, :text => status.text, :moviename => foldername )
      
    end
  end
  Reviews.each do |r|
    puts "statsu: " + r.status
  end
  writeReviewArrayToFile(reviews)
end

def getMovieInformation(inputMovies)
  @movies = Movies.new
  inputMovies.each do |m|
    i = Imdb::Search.new(m)
    imdbMovie = i.movies.first
    
    @movie = Movie.new(
      :title => imdbMovie.title,
      :id => imdbMovie.id,
      :url => imdbMovie.url,
      :rating => imdbMovie.rating,
      :votes => imdbMovie.votes
    )
    @movies.add(@movie)
  end
  @movies
end

def getAllMovieReviews(imdb_id)
  movieReviews = Imdb::MovieReview.new(imdb_id)
  puts "slut?"
  puts movieReviews.all_user_reviews
  puts "hej"
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

getAllMovieReviews("1")

puts "-----"
puts "Done, exiting"

