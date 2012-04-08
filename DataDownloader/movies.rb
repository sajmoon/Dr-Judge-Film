#require "#{File.dirname(__FILE__)}/movie"
require_relative './movie'


class Movies
  def initialize
    @movies = []
  end

  def add(movie)
    begin
      Dir::mkdir("data/#{movie.title}/")

    rescue
    end
    puts "after rescue!"
    
    File.open("data/#{movie.title}/imdbscore", "w") { |f| f.write(movie.rating) }


    @movies << movie
  end

  def size
    @movies.size
  end

  def [](id)
    @movies[id]
  end

  def each(&block)
    @movies.each { |movie| yield movie }
  end

  def xml
    xml = ""
    @movies.each { |movie| xml << movie.xml }
    xml
  end
end
