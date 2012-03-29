require "spec_helper"
require_relative "../movie"
require "factory_girl"

describe Movie do
  before do
    @movie = FactoryGirl.build(:movie)
    @movie2 = FactoryGirl.build(:movie2)
  end
  it "initializes" do
    movie = Movie.new
    movie.should_not be_nil
  end

  it "can save values" do
    @movie.id.should equal(1)
    @movie.title.should == "title"
    @movie.url.should == "url"
  end

  it "can change saved values" do
    @movie.id = 2
    @movie.title = "title2"
    @movie.url = "url2"
    @movie.id.should equal(2)
    @movie.title.should == "title2"
    @movie.url.should == "url2"
  end

  it "works with movie2" do
    @movie2.id.should == 2

  end

  it "has a hashtag" do
    @movie.hashtag.should == "#title"
  end
end
