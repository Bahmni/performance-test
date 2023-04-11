from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
import csv
import os
from dotenv import load_dotenv
load_dotenv()

chrome_options = Options()
chrome_options.add_argument('--headless')
driver = webdriver.Chrome('/usr/lib/chromium-browser/chromedriver',options=chrome_options)
options = Options()
baseUrl=os.getenv('BASE_URL')
tailUrl=os.getenv('TAIL_URL')


def scrapeReport(filename):
    url=filename+tailUrl
    print(url)
    driver.get(url)
    content = driver.page_source
    soup = BeautifulSoup(content,features='html.parser')
    headtable=soup.find('table',id='container_statistics_head').find('tbody')
    bodytable = soup.find('table',id='container_statistics_body')
    headerrows=headtable.findAll('tr')
    bodyrows = bodytable.findAll('tr')
    try:
     os.mkdir(os.getenv('OUTPUT_PATH'))
    except OSError as e:
     print('Dump Directory exists')
    writeCsvFile(filename,headerrows,bodyrows)



def writeCsvFile(filename,headerrows,bodyrows):
    with open(os.getenv('OUTPUT_PATH')+filename+'.csv', 'wt+', newline='') as f:
      writer = csv.writer(f)
      headers=[i for i in os.getenv('HEADERS').split(',')]
      writer.writerow(headers)
      for row in headerrows:
        csv_row = []
        csv_row.append(filename)
        for cell in row.findAll(['td']):
            csv_row.append(cell.text.strip())
        writer.writerow(csv_row)
      for row in bodyrows:
        csv_row = []
        csv_row.append(filename)
        for cell in row.findAll(['td']):
            s=cell.findAll('span')
            if(s):
                if(s[1].attrs.get('data-content')!=None):
                 csv_row.append(s[1].attrs.get('data-content'))
                else:
                    s=cell.text.strip()
                    result=''.join([i for i in s if not i.isdigit()])
                    csv_row.append(result)
            else:
                csv_row.append(cell.text.strip())
        writer.writerow(csv_row)


for name in os.listdir(os.getenv('REPORTS_PATH')):
    if 'bahmniclinic' in name:
      scrapeReport(name)



