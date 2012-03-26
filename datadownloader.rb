require 'twitter'
require 'builder'
require 'imdb'
require 'xmlsimple'

puts 'Dr Judge Film'
puts "-------------"
puts "v.2012-03-26"
puts "Flags: -tweets -imdb"
puts ""

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

def getTweets(hashtags)
  builder = Builder::XmlMarkup.new(:indent => 2)

  xml = ""
  xmlTweets = builder.tweets {
    hashtags.each do |h|
      Twitter.search(h, :lang => "en").map do |status|
        builder.tweet { |t| t.id(status.id); t.text(status.text); t.user(status.from_user); t.searchterm(h) }
      end
    end
  }
end

def writeToFile(xml, filename)
  puts "Writing to file: " + filename.to_s
  File.open(filename, "w") { |f| f.write(xml) }
end

def getMovieInformation(inputMovies)
  builder = Builder::XmlMarkup.new(:indent => 2)

  moviesXml = ""

  builder.comment! "Information on movies from IMDB"
  
  moviesXml = builder.movies{
    inputMovies.each do |m|
      i = Imdb::Search.new(m)
      movie = i.movies.first
      builder.movie{ |m| 
        m.id(movie.id);
        m.title(movie.title);
        m.url(movie.url);
        m.rating(movie.rating);
        m.votes(movie.votes) 
        m.hashtag("#" + movie.title[/[^(]+/].delete(' ').delete("'"));
      }
    end
  }
end

def listOfMovieTitles
  movies = ['Schindlers List', 'Inception', 'Fight Club', 'Goodfellas', 'The Matrix']
end

def getHashTagsFromXML(xmlFileName)
  hashtag = []

  movies = XmlSimple.xml_in(xmlFileName, { 'KeyAttr' => 'name' } )

  movies['movie'].each do |m|
    hashtag << m['hashtag']
  end
  hashtag
end

if imdbFlag
  puts "-----"
  puts "Get movie information from IMDB"
  movies = listOfMovieTitles
  moviesXml = getMovieInformation(movies)
  writeToFile(moviesXml, movieXmlFilename)
end

if tweetFlag 
  puts "-----"
  puts "To the tweets! "
  hashtags = getHashTagsFromXML(movieXmlFilename)
  xmlTweets = getTweets(hashtags)
  writeToFile(xmlTweets,tweetsXmlFilename)
end

puts "-----"
puts "Done, exiting"

