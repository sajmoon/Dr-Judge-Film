#require "#{File.dirname(__FILE__)}/movie"
require_relative './movie'


class Movies
  def initialize
    @movies = []
  end

  def add(movie)
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
