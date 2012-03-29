require_relative '../reviews'

describe Reviews do

  context "no reviews" do
    before :each do
      @reviews = Reviews.new
    end

    it "initializes" do
      @reviews.should_not be_nil
    end

    it "has size 0 when first initialized" do
      @reviews.size.should == 0
    end

    it "cannot return since we do not have any data in it" do
      @reviews[0].should be_nil
    end

  end

  context "one review" do
    before :each do
      @reviews = Reviews.new
      @review = Review.new(:id => "1", :text => "statustext", :source => "twitter", :movie => "movie")
      @reviews.add(@review)
    end

    it "returns size 1 when a review is added" do
      @reviews.size.should == 1
    end

    it "can return review with id 0" do
      @reviews[0].id.should == "1"
    end

    it "cannot return review with id 1" do
      @reviews[1].should be_nil
    end

  end

  context "more reviews" do
    before :each do
      @reviews = Reviews.new

      @review2 = Review.new(:id => "1", :text => "statustext", :source => "twitter", :movie => "movie")
      @review1 = Review.new(:id => "1", :text => "statustext", :source => "twitter", :movie => "movie")

      @reviews.add(@review2)
      @reviews.add(@review1)
    end

    it "has size 2" do
      @reviews.size.should == 2
    end

    it "is enuerable" do
      @reviews.each do |r|
        r.should_not be_nil
      end
    end
  end

  context "merge two reviews" do
    before :each do
      @reviews = Reviews.new
      @reviews2 = Reviews.new
      
      @r1 = Review.new(:id => 1, :text => "text", :source => "source")
      @r2 = Review.new(:id => 2, :text => "text", :source => "source")
      @r3 = Review.new(:id => 3, :text => "text", :source => "source")
      @r4 = Review.new(:id => 4, :text => "text", :source => "source")

      @r5 = Review.new(:id => 5, :text => "text", :source => "source")

    end
    it "merge two lists" do
      @reviews.add(@r1)
      @reviews.add(@r2)
      @reviews.size.should == 2
      @reviews2.add(@r3)
      @reviews2.add(@r4)
      @reviews2.add(@r5)
      @reviews2.size.should == 3

      @reviews.merge(@reviews2)
      @reviews.size.should == 5

    end
  end

end
