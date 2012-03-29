require "spec_helper"

require_relative "../movie"
require_relative "../review"
require_relative "../twitter_parser.rb"

describe TwitterParser do
  context "twitter" do
    before do
      @parser = TwitterParser.new
    end

    it "initializes" do
      @parser.should_not be_nil
    end
    
    context "for a movie" do
      before do
        @movie = FactoryGirl.build(:movie)
      end

      it "should return tweets for a movie" do
        @reviews = @parser.getTweetsForOneMovie(@movie)
        @reviews.size.should_not == 0
      end
    end

    context "with movies" do
      before :each do 
        @movie = FactoryGirl.build(:inception)
        @movie2 = FactoryGirl.build(:lotr)
        @movies = Movies.new
        @movies.add(@movie)
        @movies.add(@movie2)
      end

      it "getTweets takes movies as paramters" do
        @movies.size.should == 2
        @reviews = @parser.getTweetsFromMovies(@movies)

        @reviews.should be_instance_of(Reviews)

        @reviews.size.should_not == 0

      end
    end
  end

end
