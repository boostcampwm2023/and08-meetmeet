import * as fs from 'fs';
import { diskStorage } from 'multer';
import { extname } from 'path';
import { v4 as uuidv4 } from 'uuid';

export const multerOptions = {
  storage: diskStorage({
    destination: (req, file, callback) => {
      const filePath: string = 'uploads';

      if (!fs.existsSync(filePath)) {
        fs.mkdirSync(filePath);
      }

      callback(null, filePath);
    },

    filename: (req, file, callback) => {
      callback(null, generateRandomFileName(file));
    },
  }),
};

const generateRandomFileName = (file: Express.Multer.File) => {
  return `${uuidv4()}${extname(file.originalname)}`;
};
