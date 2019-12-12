from flask import Flask, render_template,send_file, flash, request, redirect
import os
import urllib.request
from werkzeug.utils import secure_filename
import time
app = Flask(__name__)


###

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import matplotlib.mlab as mlab
from matplotlib.patches import Rectangle
from pandas.plotting import table
from matplotlib.backends.backend_pdf import PdfPages
from matplotlib.backends.backend_agg import FigureCanvasAgg
from PyPDF2 import PdfFileMerger
from weasyprint import HTML
import os
import math

UPLOAD_FOLDER = './data/'


app.secret_key = "secret key"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024

def valid_pdf(filename):
	extension = filename.split('.')[-1]
	if extension == 'pdf':
		return True
	else:
		return False

def valid_csv(filename):
	extension = filename.split('.')[-1]
	if extension == 'csv':
		return True
	else:
		return False

@app.route('/')
def home():
	return render_template('index.html')

@app.route('/pdftocsv')
def extractor_page():
	return render_template('pdftocsv.html')

@app.route('/analytics')
def analytics_page():
	return render_template('analytics.html')

@app.route('/pdftocsv', methods=['POST'])
def pdf_extractor():
	if request.method == 'POST':
        # check if the post request has the file part
		if 'file' not in request.files:
			flash('No file part')
			return redirect(request.url)
		file = request.files['file']
		if file.filename == '':
			flash('No file selected for uploading')
			return redirect(request.url)

		if valid_pdf(file.filename):
			filename = secure_filename(file.filename)
			file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
			#flash('File successfully uploaded')
			year = request.form['year']
			semester = request.form['semester']
			print("java -jar runner.jar " + filename + " comp " + year + " " + semester)
			os.system("java -jar runner.jar " + filename + " comp " + year + " " + semester)
			time.sleep(5)
			extracted_file = filename.split('.')[0]+".csv"
			print(extracted_file)
			return send_file(extracted_file, as_attachment=True)
		else:
			flash('File must be in the form of pdf')
			return redirect(request.url)

@app.route('/analytics', methods = ['POST'])
def generate_report():
	if request.method == 'POST':
		if 'file' not in request.files:
			flash('No file part')
			return redirect(request.url)
		file = request.files['file']

		if file.filename == '':
			flash('No file selected for uploading')
			return redirect(request.url)
		
		if valid_csv(file.filename):
			filename = secure_filename(file.filename)
			file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
			year = request.form['year']
			semester = request.form['semester']
			
			analysis_file = visualizer(os.path.join(app.config['UPLOAD_FOLDER'],filename), year, semester)
			#print("java -jar runner.jar " + filename + " comp " + year + " " + semester)
			#os.system("java -jar runner.jar " + filename + " comp " + year + " " + semester)
			#time.sleep(5)
			#extracted_file = filename.split('.')[0]+".csv"
			#print(extracted_file)
			return send_file(analysis_file, as_attachment=True)


def set_config(year,sem):
	year = int(year)
	print('year = ',year)
	config_file = ""
	if year == 2:
		config_file += "se_"
	elif year == 3:
		config_file += "te_"
	elif year == 4:
		config_file += "be_"
	
	#department
	config_file += "comp_"

	config_file += str(sem)

	reader = open('/home/ash/workspace/reparse-playground/analytics-configuration/'+config_file)
	subjects = reader.readline().strip().split(',')
	pracs = reader.readline().strip().split(',')
	grades = reader.readline().strip().split(',')
	print(grades)
	return subjects,pracs,grades

def visualizer(filename,year,sem):
	data = pd.read_csv(filename)
	subjects,pracs,grades = set_config(year,sem)
	filename = filename.split('/')[-1].split('.')[0]
	subjects_df = data.filter(subjects,axis = 1)
	pracs_df = data.filter(pracs, axis = 1)
	grades_df = data.filter(grades, axis = 1)

	stats = subjects_df.describe()
	stats.to_html('subjects.html')
	pracs_df.rename(columns={'BE_PROJ_STAGE1': 'BP1', 'BE_PROJ_STAGE2_TW':'BP2_TW', 'BE_PROJ_STAGE2_OR' : 'BP2_OR'}, inplace=True)
	stats = pracs_df.describe()
	print("pracs_df columns", pracs_df.columns)
	stats.to_html('pracs.html')
	stats = grades_df.describe()
	stats.to_html('grades.html')
	process_html(['subjects.html', 'pracs.html', 'grades.html'])
	HTML('merged_tables.html').write_pdf('temp_stats.pdf')
	
	bar_charts_theory = plot_bar_charts(subjects_df, 1)
	
	pp = PdfPages('temp_plots.pdf')
	for curr in bar_charts_theory:
		pp.savefig(curr)
		plt.close(curr)
	bar_charts_practicals = plot_bar_charts(pracs_df, 2)
	for curr in bar_charts_practicals:
		pp.savefig(curr)
		plt.close(curr)
	#heatmap = plot_heatmap(subjects_df)
	#pp.savefig(heatmap)

	#heatmap = plot_heatmap(pracs_df)
	#pp.savefig(heatmap)'''

	pie_charts = plot_pie_charts(grades_df)
	
	for curr in pie_charts:
		try:
			pp.savefig(curr)
			curr.close()
		except:
			continue
	pp.close()
	
	#merge pdfs
	pdfs = ['temp_stats.pdf','temp_plots.pdf']

	merger = PdfFileMerger()

	for pdf in pdfs:
	    merger.append(pdf)

	merger.write(filename+"_analysis.pdf")
	merger.close()
	os.system("rm temp_stats.pdf")
	os.system("rm temp_plots.pdf")
	
	return filename+"_analysis.pdf"

def process_html(file_list):

	reader = open('header')
	prefix = reader.read()
	reader.close()
	content = ""

	for file_name in file_list:
		reader = open(file_name)
		content += reader.read()
		content += "<br/>"
		reader.close()


	writer = open('merged_tables.html', 'w')
	writer.write(prefix+content)

def plot_bar_charts(subjects, mode):
	bounds = [40, 60, 66, 80,100]
	if mode == 2:
		bounds = [int(i/2) for i in bounds]
	plot_list = [plt.figure()]
	
	#print(subjects.columns)
	
	for subject in subjects.columns:
		data = subjects[subject]
		print("plotting Subject: ", subject)

		#print("Data: " , data)
		if subject == 'BP2_TW':
			continue
		
		score_map = {}		
		for i in range(0,int(bounds[-1])+1):
			score_map[i] = 0
		
		for val in data:
			#print('val: ', val)
			score_map[val] += 1
			#print('frequency = ',score_map[val])
		print("Score map: ", score_map)
		reduce_map = {}
		#reduce_map[0]=0
		for i in range(0,int(bounds[-1]/3+1)):
			count = 0
			for j in range(0, 3):
				if (3*i + j > 100):
					break
				count += score_map[3*i + j]
				print(3*i+j, end = " ")
			print('range ', 3*i + 2 , ' = ',count)
			reduce_map[3*i] = count
			reduce_map[3*i+1] = count
			reduce_map[3*i+2] = count
		print(reduce_map)

		keys = [i for i in range(0,bounds[0])]
		values = [reduce_map[i] for i in keys]

		plt.bar(keys,values,color='maroon')

		keys = [i for i in range(bounds[0],bounds[1])]
		values = [reduce_map[i] for i in keys]

		plt.bar(keys,values,color='orange')

		keys = [i for i in range(bounds[1],bounds[2])]
		values = [reduce_map[i] for i in keys]

		plt.bar(keys,values,color='yellow')

		keys = [i for i in range(bounds[2],bounds[3])]
		values = [reduce_map[i] for i in keys]

		plt.bar(keys,values,color='green')

		keys = [i for i in range(bounds[3],bounds[4])]
		values = [reduce_map[i] for i in keys]

		plt.bar(keys,values,color='blue')
		plt.xticks([5*i for i in range(0,int(bounds[0]/2) + 1)])

		handles = [Rectangle((0,0),1,1,color=c,ec="k") for c in ['maroon', 'orange','yellow','green', 'blue']]
		labels= ["Fail","Second class", "First Class", "Distinction", "Outstanding"]
		plt.legend(handles, labels)

		plt.xlabel(subject, fontsize=16)  
		plt.ylabel("Students", fontsize=16)
		#plt.show()
		
		plot_list.append(plt.figure())
		
	return plot_list

def plot_heatmap(data):
	corrmat = data.corr() 
  
	f, ax = plt.subplots(figsize =(9, 8)) 
	sns_fig = sns.heatmap(corrmat, ax = ax, cmap ="YlGnBu", annot = True, linewidths = 0.1)
	sns_fig.show()	
	return sns_fig.get_figure()

def plot_pie_charts(grades_df):
	pie_plot_list = []


	for year_grade in grades_df.columns:
		grade_count = {}
		grade_count[0] = 0
		for i in range(5,11):
			grade_count[i] = 0

		data = grades_df[year_grade]

		for val in data:
			#print(val)
			grade_count[math.floor(val)] += 1

		#print(grade_count)
		less_than_7 = grade_count[0] + grade_count[5] + grade_count[6]
		plot_list = [less_than_7]
		for i in grade_count.keys():
			if i not in [0,5,6]:
				plot_list.append(grade_count[i])

		print(plot_list)

		labels = ["< 7"]
		for i in range(7,10):
			labels.append(str(i)+" to " + str(i+1) )
		labels.append(str(10))
		print(labels)


		#colors = ['gold', 'yellowgreen', 'lightcoral', 'lightskyblue']
		explode = (0,0,0, 0.1, 0)

		plt.pie(plot_list,autopct='%1.1f%%', pctdistance=1.35, explode = explode,labeldistance=1.1)
		plt.axis('equal')
		plt.legend(labels)
		plt.xlabel(year_grade)
		#plt.show()
		#myfig.savefig('asdf.png')		

		pie_plot_list.append(plt.figure())
		
	return pie_plot_list

#analytics('/home/ash/workspace/reparse-playground/be_comp_may.csv',4,2)

if __name__ == '__main__':
	app.run(debug=True)
