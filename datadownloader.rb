require 'twitter'
require 'builder'

puts 'Dr Judge Film'
puts "-------------"
puts "v.2012-03-26"
puts "Downloading tweets"

builder = Builder::XmlMarkup.new

xml = ""
Twitter.search("#LordOfTheRings", :lang => "en", :rpp => 3).map do |status|
  "#{status.from_user}: #{status.text}"
  xml = xml + builder.tweet { |t| t.text(status.text); t.user(status.from_user) }
end

puts "Writing to file"
File.open( "xmldata" , 'w') { |f| f.write(xml) }


puts "Done, exiting"


