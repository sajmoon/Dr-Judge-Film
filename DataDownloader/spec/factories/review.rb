require "factory_girl"

FactoryGirl.define do
  factory :review do
    id 1
    text "text"
    source "source"
    moviename "moviename"
  end
end
