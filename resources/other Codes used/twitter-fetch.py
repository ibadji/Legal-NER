from selenium import webdriver
from selenium.webdriver.common.keys import Keys

#the chromedriver is added to the python file
browser = webdriver.Chrome()

url = u'https://twitter.com/search?l=es&q=Ley%20near%3A%22spain%22%20within%3A15mi&src=typd' 

browser.get(url)

body = browser.find_element_by_tag_name('body')

for _ in range(1000):
    body.send_keys(Keys.PAGE_DOWN)

tweets = browser.find_elements_by_class_name('tweet-text')

file = open("testfile.txt","w") 

count = 0
for tweet in tweets:
    file.write("%d--------" %(count))
    file.write(tweet.text+ "\n")
    count = count + 1

file.close()
