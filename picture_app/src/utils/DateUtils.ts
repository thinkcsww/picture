const DateUtils = {
  getRemainTime: (date: string) => {
    const now = new Date();
    const dueDate = new Date(date);
    const diff = dueDate.getTime() - now.getTime();

    const days = parseInt(String((diff) / (1000 * 60 * 60 * 24)));
    const hours = parseInt(String(Math.abs(diff) / (1000 * 60 * 60) % 24));
    const minutes = parseInt(String(Math.abs(diff) / (1000 * 60) % 60));
    const seconds = parseInt(String(Math.abs(diff) / (1000) % 60));

    let due = '';

    if (days > 0) {
      due += `${days}일 `
    }

    if (hours > 0) {
      due += `${hours}시간 `
    }

    if (minutes > 0) {
      due += `${minutes}분`
    }

    if (due.trim() === '') {
      due = `${seconds}초`
    }

    return due;
  },
  getFormattedDate: (date: string) => {
    const d = new Date(date);

    return `${d.getFullYear()}년 ${d.getMonth() + 1}월 ${d.getDate()}일 ${d.getHours()}시 ${d.getMinutes()}분`
  },
  getFormattedMessageDate: (date: string) => {
    const d = new Date(date);

    return `${d.getHours() >= 12 ? '오후' : '오전'} ${d.getHours() % 12}시 ${d.getMinutes()}분`
  },
  getPastFormattedDate: (time: any) => {
    let date = new Date(time);

    // utc -> local로 변경
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    let diff = (new Date().getTime() - date.getTime()) / 1000;


    if (diff < 0) {
      diff = 0;
    }


    let day_diff = Math.floor(diff / 86400);

    if (isNaN(day_diff) || day_diff < 0) {
      return;
    }
    if( day_diff == 0 ){
      if( diff < 60 ) return  '방금전';
      else if( diff < 120 ) return '1분전';
      else if(diff < 3600 ) return Math.floor(diff / 60) + '분전';
      else if (diff < 7200 ) return '1시간전';
      else if  (diff < 86400 ) return Math.floor(diff / 3600) + '시간전';
    }

    if( day_diff == 1 ) return '어제';

    if (day_diff < 7) return  day_diff + '일전';
    if(day_diff < 31 ) return Math.floor(day_diff / 7) + '주전';
    if(day_diff < 360 ) return Math.floor(day_diff / 30) + '개월 전';
    if(day_diff >= 360) return(Math.floor(day_diff / 360) == 0 ? 1 : Math.floor(day_diff / 360)) + ' 년 전';
    // else return (date.getFullYear() + "").slice(-2)  + "년 " + ('0' + (date.getMonth() + 1)).slice(-2) + "월 " + ('0' + date.getDate()).slice(-2) + "일";

  },
}

export default DateUtils;
