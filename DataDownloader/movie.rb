class Movie

  attr_accessor :id, :url, :title, :rating, :votes
  def initialize(inputHash = nil)
    if !inputHash.nil?
      @id = inputHash[:id]
      @url = inputHash[:url]
      @title = inputHash[:title]
      @rating = inputHash[:rating]
      @votes = inputHash[:votes]
    end
  end

  def hashtag
    hashtag = ("#" + self.title[/[^(]+/].delete(' ').delete("'"));
  end

  def xml
    title
  end
end
