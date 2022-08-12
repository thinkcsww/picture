import { Specialty } from "../types/Common";

const CommonUtils = {
  specialtyToLabel: (specialty: Specialty) => {
    if (specialty === Specialty.PEOPLE) {
      return '인물';
    } else if (specialty === Specialty.OFFICIAL) {
      return '증명사진';
    } else if (specialty === Specialty.BACKGROUND) {
      return '배경';
    } else if (specialty === Specialty.ETC) {
      return '기타';
    }
  }
}

export default CommonUtils;
