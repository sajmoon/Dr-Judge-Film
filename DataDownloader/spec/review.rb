require_relative "../review"

describe Review do
  it "initialize" do
    review = Review.new
    review.should_not be_nil
  end

  context "A review in general" do
    before :each do
      @review = Review.new(:id => "id", :text => "text", :source => "source", :moviename => "moviename")
    end
    
    it "should initialize" do
      @review.should_not be_nil
    end

    it "should return the values" do
      @review.id.should == "id"
      @review.text.should == "text"
      @review.source.should == "source"
      @review.moviename.should == "moviename"
    end

    it "should change the values" do
      @review.id.should == "id"
      @review.id = "id2"
      @review.id.should == "id2"
    end

    it "should still use the before filtes values after change in prev test" do
      @review.id.should == "id"
    end
  end
end
