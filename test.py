import pandas as pd
import datetime as dt

def read_rent_football_ground(page_num, ground_num):

    target_url = ('http://www.ansanuc.net/group/11604/30102/application/applicantList.do?'
              'currentPageNo=' + str(page_num) +
              '&searchCondition=&searchKeyword='
              '&searchBigCategoryIdx=1'
              '&searchMiddleCategoryIdx=' + str(ground_num))

    data = pd.read_html(target_url)
    data = data[0]
    data.columns = ['번호','신청일시','사용일시','대표자명(대관팀명)','행사명','시설구분']
    edit_data = data.dropna(axis=0, how='any')
    edit_data = edit_data.drop(columns=['번호','신청일시','행사명'])
    edit_data = edit_data.reset_index(drop=True)
    return edit_data

def pages_to_df():
    df_list_data = []
    page = 1
    ground = 1
    while True:
        if ground > 15:
            break

        while True:
            try:
                data = read_rent_football_ground(page, ground)
                print(data['시설구분'])
                if page > 9:
                    break
                df_list_data.append(data)
                page = page + 1
            except:
                break
        page = 1
        ground = ground + 1

    df_price = pd.concat(df_list_data)
    df_price = df_price.reset_index(drop=True)
    return df_price

df = pages_to_df()
print(df)

# data_frame = pd.read_csv('list.csv',names=['name','age','job'])
# print(data_frame.name)

#
# def read_stock_price_page(stock_code, page_num):
#     target_url = ('http://finance.naver.com/item/sise_day.nhn?code='
#                   +stock_code + '&page=' + str(page_num))
#     data = pd.read_html(target_url)
#     data = data[0]
#     data.columns = ['날짜','당일종가','전일비','시가','고가','저가','거래량']
#     price_data = data.dropna(axis=0, how='any')
#     price_data = price_data.drop(price_data.index[0])
#     price_data = price_data.reset_index(drop=True) #인덱스를 재설정
#     price_data['날짜'] = pd.to_datetime(price_data['날짜'], format='%Y/%m/%d')
#
#     print(price_data)
#     return price_data
#
# def stock_price_pages_to_df(code, days_limit=30):
#     df_list_price = []
#     page = 1
#     while True:
#         try:
#             data = read_stock_price_page(code, page)
#             time_limit = dt.datetime.now() - data['날짜'][0]
#             if time_limit.days > days_limit: break
#             df_list_price.append(data)
#             page = page + 1
#
#         except: break
#     df_price = pd.concat(df_list_price)
#     df_price = df_price.reset_index(drop=True)
#     return df_price
#
#
# stock_code = '060720'
# days_limit = 30
# df = stock_price_pages_to_df(stock_code, days_limit)
#
# print(df)