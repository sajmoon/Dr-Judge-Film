require_relative "../movies"
require "factory_girl"

require File.expand_path("spec/factories/movie.rb")

describe Movies do
  before :each do
    @movie = FactoryGirl.build(:movie)
    @movie2 = FactoryGirl.build(:movie, :id => 2, :title => "movie2")
    @movies = Movies.new
  end

  it "has 0 size" do
    @movies.size.should == 0
  end

  it "can add stuff" do
    @movies.add(@movie)
    @movies.size.should == 1
    @movies.add(@movie2)
    @movies.size.should == 2
  end

  it "we can see all items one by one" do
    @movies.add(@movie)
    @movies.add(@movie2)
    @movies[0].id.should == 1
    @movies.size.should == 2
    @movies[1].id.should == 2
  end

  it ".each" do
    @movies.each do |movie|
      movie.should be_instance_of(Movie)
    end
  end

end
