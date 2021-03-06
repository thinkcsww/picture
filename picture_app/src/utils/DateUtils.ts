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
  }
}

export default DateUtils;
