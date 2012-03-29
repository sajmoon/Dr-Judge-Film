require "factory_girl"

FactoryGirl.define do
  factory :movie do
    title 'title'
    url "url"
    id 1
  end

  factory :movie2, :class => Movie do
    title "movie2"
    url "url2"
    id 2
  end

  factory :inception, :class => Movie do
    title "inception"
    url "url2"
    id 2
  end

  factory :lotr, :class => Movie do
    title "Lord Of The Rings"
    url "url2"
    id 2
  end

end
