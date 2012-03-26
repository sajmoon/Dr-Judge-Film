require 'twitter'
require 'builder'
require 'imdb'

puts 'Dr Judge Film'
puts "-------------"
puts "v.2012-03-26"
puts "Downloading tweets"

def getTweets

  builder = Builder::XmlMarkup.new(:indent => 2)

  xml = ""
  searchfor = [ "#LordOfTheRing", "#" ]

  Twitter.search(searchfor, :lang => "en").map do |status|
    "#{status.from_user}: #{status.text}"
    xml = xml + builder.tweet { |t| t.text(status.text); t.user(status.from_user); t.searchterm(searchfor) }
  end

  xml
end

def writeToFile(xml, filename)
  puts "Writing to file"
  File.open(filename, "w") { |f| f.write(xml) }
end

def getMovieInformation(movies)
  builder = Builder::XmlMarkup.new(:indent => 2)

  moviesXml = ""
  
  movies.each do |m|
    i = Imdb::Search.new(m)
    movie = i.movies.first
    moviesXml = moviesXml + builder.movie{ |m| m.id(movie.id); m.title(movie.title); m.url(movie.url); m.rating(movie.rating) }
  end

  moviesXml
end

def listOfMovieTitles
  movies = ['Schindlers List', 'Inception', 'Fight Club', 'Goodfellas', 'The Matrix']
end

puts "Get movie information from IMDB"
movies = listOfMovieTitles

moviesXml = getMovieInformation(movies)

writeToFile(moviesXml, "movieData.xml")


xmlTweets = getTweets

writeToFile(xmlTweets,"tweetData.xml")



puts "Done, exiting"

