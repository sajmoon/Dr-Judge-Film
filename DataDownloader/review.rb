class Review
  attr_accessor :id, :text, :source, :moviename

  def initialize(inputHash = nil)
    if !inputHash.nil?
      @id = inputHash[:id]
      @text = inputHash[:text]
      @source = inputHash[:source]
      @moviename = inputHash[:moviename]
    end
  end

  def saveToDisc()
    begin 
      Dir::mkdir(foldername)
    rescue
    end

    File.open(foldername + @id.to_s, "w") { |f| f.write(@text) }
  end

  def foldername
    "data/#{@moviename.delete(":")}/#{@source}/"
  end
end
