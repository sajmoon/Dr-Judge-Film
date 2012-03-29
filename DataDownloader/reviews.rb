require_relative "review"

class Reviews
  #def self.each(&args)
  #  @reviews.each(&args)
  #end
  attr_accessor :reviews

  def initialize()
    @reviews = []
  end

  def each(&block)
    @reviews.each { |review| yield review }
  end

  def merge(reviews)
    newReviews = @reviews | reviews.reviews
    @reviews = newReviews
  end

  def add(review) 
    @reviews << review
  end

  def size()
    @reviews.size
  end

  def [](id)
    @reviews[id]
  end

  def saveToDisc()
    @reviews.each do |review|
      review.saveToDisc()
    end
  end

end

