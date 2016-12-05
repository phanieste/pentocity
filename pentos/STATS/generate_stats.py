import pandas as pd ###
import os ###

RESULTS_DIR = './pentos/RESULTS/individual/' ###
STATS_DIR = './pentos/STATS/'

teamNames = ['g'+str(x) for x in range(1,11) if x!=7] ###

runNames = ['run'+str(x) for x in range(10)] ###

def readIndividualResult( f ) :                                                                                     
    df = pd.read_csv(os.path.join(RESULTS_DIR, f), header=None, names=runNames)
    dfInfo = pd.DataFrame({'dist':[f]*len(teamNames), 'team':teamNames})
    return df.join(dfInfo)
##################################

data = pd.concat([readIndividualResult(f) for f in os.listdir(RESULTS_DIR)]).set_index(['team','dist']) ###
filterNonNegative = lambda temp: temp[temp[0]>=0] ###
getStatsBy = lambda colnames : filterNonNegative(data.stack().reset_index()).groupby(colnames).describe()[0].unstack().reset_index().set_index(colnames) ###

if(True):
    getStatsBy(['team', 'dist']).to_csv('./pentos/STATS/stats-team-dist.csv') ###
    getStatsBy(['team', 'dist']).sort('mean', ascending=False).to_csv('./pentos/STATS/stats-team-dist-sorted.csv') ###
    getStatsBy(['team']).sort('mean', ascending=False).to_csv('./pentos/STATS/stats-team.csv') ###
    getStatsBy(['dist']).sort('mean', ascending=False).to_csv('./pentos/STATS/stats-dist.csv') ###

if(True):
    df1 = getStatsBy(['team', 'dist'])
    s1 = df1['mean']
    s1.unstack().to_csv('./pentos/STATS/stats-team-dist-means.csv')
    s1.unstack('team').corr().to_csv('./pentos/STATS/stats-team-corr.csv')
    s1.unstack('dist').corr().to_csv('./pentos/STATS/stats-team-dist.csv')
    
    s1 = df1[['mean']].reset_index() ###                                                                                                                      
    top_distributions = getStatsBy(['dist']).sort('mean')['mean'] ###
    s1['dist_mean'] = s1['dist'].map( top_distributions ) ###
    top_3_by_distribution = s1.sort('mean', ascending=False).sort(['dist_mean','mean']).groupby('dist').tail(3) ###
    top_3_by_distribution.to_csv('./pentos/STATS/stats-top_3_by_distribution.csv')

