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
imdbReviewFlag = false
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
    puts "Movie: #{m}"
    begin
      @movie = getMovieByTitle(m)
      @movies.add(@movie)
    rescue
      puts " - Not found"
    end
  end
  @movies.save
  @movies
end

def getAllMovieReviewsForAllMovies(movies)
  allReviews = Reviews.new
  movies.each do |movie|
    #allReviews.merge(getAllMovieReviews(movie))
    reviews = getAllMovieReviews(movie)
    #reviews.saveToDisc()
  end
  allReviews
end

def getAllMovieReviews(movie)
  imdbMovie = Imdb::Movie.new(movie.id)
  
  readyToStop = false
  i = 450 #Fifth element
  #i = 0
  j = 50
  steplenght = j

  while !readyToStop

    @reviews = Reviews.new
    imdbMovie.user_reviews(i,j).each_with_index do |review, index|
      newReview = Review.new(:id => review.author.to_s + "_" + review.created_at.to_s, :text => review.to_s, :source => "imdb", :moviename => movie.title)
      @reviews.add(newReview)
    end

    @reviews.saveToDisc()

    puts "Total saved #{@reviews.size}"
    if @reviews.size >= j
      puts "set new values"
      i = i+ j
      j = steplenght
    else
      readyToStop = true
    end

  end

end

def listOfMovieTitles
  movies1 = ['The girl with the dragon tattoo', 'The Raid: Redemption', 'Hobo with a shotgun', 'Resident Evil: Afterlife', 'Resident Evil', 'Resident Evil: Extinction', 'Resident Evil: Apocalypse', 'Resident Evil: Degeneration', 'Resident Evil', 'Zoolander', 'Dummy', 'The Claim', 'The Million dollar hotel', 'The messenger: The Story of Joan of Arc', 'The fifth Element', 'He Got Game', 'Dazed and confused', 'Chaplin' , 'Kuffs', 'Return to the blue lagoon', 'Married with Children', 'Parker Lewis', 'Two moon Junction']
movies2 = ['wrath of the titans' , 'We bought a zoo', 'Mirror Mirror', 'The three stooges', 'The artist', 'The Muppets', 'Midnight in Paris', 'Crazy, Stupid, Love', 'American Pie', 'Bridesmaids', 'The hangover', 'Forrest Gump', 'Back to the future', 'Kick-Ass', 'The big Year']
movies3 = ['Orgazmo', 'Hot Shots!', 'Prometheus', 'The Wire', 'Alien', 'The Boondock Saints', 'John Carter', 'In Time', 'X-men: First class', 'Battle Royal', 'Green Lantern', 'Melancholia', 'Transformers: Dark of the moon', 'Men in Black III', 'Eternal Sunshine of the Spotless Mind', 'Dr Jeckell and Mr.Hyde']
movies4 = ['Inception', 'Schindlers List', 'Inception', 'Fight Club', 'Goodfellas', 'The Matrix', 'Shawshank Redeption', 'Fight Club', 'Goodfellas']
movies = movies1 | movies2 | movies3 | movies4
end

if imdbFlag
  puts "-----"
  puts "Get movie information from IMDB"
  movielist = listOfMovieTitles
  @movies = getMovieInformation(movielist)
  
  @movies.save
  #writeToFile(@movies, movieXmlFilename)
end

if tweetFlag 
  puts "-----"
  puts "To the tweets! "
  @twitterParser = TwitterParser.new
  @reviews = @twitterParser.getTweetsFromMovies(@movies)

  @reviews.saveToDisc()
  #xmlTweets = getTweets(hashtags)
  #writeToFile(xmlTweets,tweetsXmlFilename)
end

if imdbReviewFlag
  puts "-----"
  puts "And onwards to the reviews!"

#@movie = getMovieById("1375666")
  @reviews = getAllMovieReviewsForAllMovies(@movies)
end



#@reviews.saveToDisc()

puts "-----"
puts "Done, exiting"

