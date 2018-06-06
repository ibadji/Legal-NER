#https://github.com/haccer/twint
import twint

c = twint.Config()

c.Lang = "es"
c.Search = "Ley"
c.Near = "spain"
##Since = "2018-01-01"
##Until = "2018-06-06"
Limit = 1000
c.Store_csv = True
# CSV Fieldnames
c.Custom = ["id", "user_id", "username", "tweet","link"]
c.Output = "twitter.csv"
twint.run.Search(c)



##from selenium import webdriver
##from selenium.webdriver.common.keys import Keys
##from bs4 import BeautifulSoup
##from pprint import pprint
##
###the chromedriver is added to the python file
##browser = webdriver.Chrome()
##
##url = u'https://twitter.com/search?l=es&q=Ley%20near%3A%22spain%22%20within%3A15mi&src=typd' 
##browser.get(url)
##body = browser.find_element_by_tag_name('body')
##
##for _ in range(20):
##    body.send_keys(Keys.PAGE_DOWN)
##
##tweets = browser.find_elements_by_class_name('original-tweet')
##
##file = open("tweets.txt","w") 
##
##count = 0
##for tweet in tweets:
##    print("%d" %(count))
##    print(tweet.text+ "\n")
##    count = count + 1
##file.close()
