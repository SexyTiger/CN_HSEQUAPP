package net.huansi.equipment.equipmentapp.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import net.huansi.equipment.equipmentapp.sqlite_db.CheckDataInSQLite;
import net.huansi.equipment.equipmentapp.sqlite_db.EquipmentWithoutRFIDInSQLite;
import net.huansi.equipment.equipmentapp.sqlite_db.Inventory;
import net.huansi.equipment.equipmentapp.sqlite_db.InventoryDetail;
import net.huansi.equipment.equipmentapp.sqlite_db.MaterialDataInSQLite;
import net.huansi.equipment.equipmentapp.sqlite_db.RepairBaseDataInSQLite;
import net.huansi.equipment.equipmentapp.sqlite_db.RepairDetailInSQLite;
import net.huansi.equipment.equipmentapp.sqlite_db.RepairEquipmentInSQLite;
import net.huansi.equipment.equipmentapp.sqlite_db.RepairHdrInSQLite;

import net.huansi.equipment.equipmentapp.gen.CheckDataInSQLiteDao;
import net.huansi.equipment.equipmentapp.gen.EquipmentWithoutRFIDInSQLiteDao;
import net.huansi.equipment.equipmentapp.gen.InventoryDao;
import net.huansi.equipment.equipmentapp.gen.InventoryDetailDao;
import net.huansi.equipment.equipmentapp.gen.MaterialDataInSQLiteDao;
import net.huansi.equipment.equipmentapp.gen.RepairBaseDataInSQLiteDao;
import net.huansi.equipment.equipmentapp.gen.RepairDetailInSQLiteDao;
import net.huansi.equipment.equipmentapp.gen.RepairEquipmentInSQLiteDao;
import net.huansi.equipment.equipmentapp.gen.RepairHdrInSQLiteDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig checkDataInSQLiteDaoConfig;
    private final DaoConfig equipmentWithoutRFIDInSQLiteDaoConfig;
    private final DaoConfig inventoryDaoConfig;
    private final DaoConfig inventoryDetailDaoConfig;
    private final DaoConfig materialDataInSQLiteDaoConfig;
    private final DaoConfig repairBaseDataInSQLiteDaoConfig;
    private final DaoConfig repairDetailInSQLiteDaoConfig;
    private final DaoConfig repairEquipmentInSQLiteDaoConfig;
    private final DaoConfig repairHdrInSQLiteDaoConfig;

    private final CheckDataInSQLiteDao checkDataInSQLiteDao;
    private final EquipmentWithoutRFIDInSQLiteDao equipmentWithoutRFIDInSQLiteDao;
    private final InventoryDao inventoryDao;
    private final InventoryDetailDao inventoryDetailDao;
    private final MaterialDataInSQLiteDao materialDataInSQLiteDao;
    private final RepairBaseDataInSQLiteDao repairBaseDataInSQLiteDao;
    private final RepairDetailInSQLiteDao repairDetailInSQLiteDao;
    private final RepairEquipmentInSQLiteDao repairEquipmentInSQLiteDao;
    private final RepairHdrInSQLiteDao repairHdrInSQLiteDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        checkDataInSQLiteDaoConfig = daoConfigMap.get(CheckDataInSQLiteDao.class).clone();
        checkDataInSQLiteDaoConfig.initIdentityScope(type);

        equipmentWithoutRFIDInSQLiteDaoConfig = daoConfigMap.get(EquipmentWithoutRFIDInSQLiteDao.class).clone();
        equipmentWithoutRFIDInSQLiteDaoConfig.initIdentityScope(type);

        inventoryDaoConfig = daoConfigMap.get(InventoryDao.class).clone();
        inventoryDaoConfig.initIdentityScope(type);

        inventoryDetailDaoConfig = daoConfigMap.get(InventoryDetailDao.class).clone();
        inventoryDetailDaoConfig.initIdentityScope(type);

        materialDataInSQLiteDaoConfig = daoConfigMap.get(MaterialDataInSQLiteDao.class).clone();
        materialDataInSQLiteDaoConfig.initIdentityScope(type);

        repairBaseDataInSQLiteDaoConfig = daoConfigMap.get(RepairBaseDataInSQLiteDao.class).clone();
        repairBaseDataInSQLiteDaoConfig.initIdentityScope(type);

        repairDetailInSQLiteDaoConfig = daoConfigMap.get(RepairDetailInSQLiteDao.class).clone();
        repairDetailInSQLiteDaoConfig.initIdentityScope(type);

        repairEquipmentInSQLiteDaoConfig = daoConfigMap.get(RepairEquipmentInSQLiteDao.class).clone();
        repairEquipmentInSQLiteDaoConfig.initIdentityScope(type);

        repairHdrInSQLiteDaoConfig = daoConfigMap.get(RepairHdrInSQLiteDao.class).clone();
        repairHdrInSQLiteDaoConfig.initIdentityScope(type);

        checkDataInSQLiteDao = new CheckDataInSQLiteDao(checkDataInSQLiteDaoConfig, this);
        equipmentWithoutRFIDInSQLiteDao = new EquipmentWithoutRFIDInSQLiteDao(equipmentWithoutRFIDInSQLiteDaoConfig, this);
        inventoryDao = new InventoryDao(inventoryDaoConfig, this);
        inventoryDetailDao = new InventoryDetailDao(inventoryDetailDaoConfig, this);
        materialDataInSQLiteDao = new MaterialDataInSQLiteDao(materialDataInSQLiteDaoConfig, this);
        repairBaseDataInSQLiteDao = new RepairBaseDataInSQLiteDao(repairBaseDataInSQLiteDaoConfig, this);
        repairDetailInSQLiteDao = new RepairDetailInSQLiteDao(repairDetailInSQLiteDaoConfig, this);
        repairEquipmentInSQLiteDao = new RepairEquipmentInSQLiteDao(repairEquipmentInSQLiteDaoConfig, this);
        repairHdrInSQLiteDao = new RepairHdrInSQLiteDao(repairHdrInSQLiteDaoConfig, this);

        registerDao(CheckDataInSQLite.class, checkDataInSQLiteDao);
        registerDao(EquipmentWithoutRFIDInSQLite.class, equipmentWithoutRFIDInSQLiteDao);
        registerDao(Inventory.class, inventoryDao);
        registerDao(InventoryDetail.class, inventoryDetailDao);
        registerDao(MaterialDataInSQLite.class, materialDataInSQLiteDao);
        registerDao(RepairBaseDataInSQLite.class, repairBaseDataInSQLiteDao);
        registerDao(RepairDetailInSQLite.class, repairDetailInSQLiteDao);
        registerDao(RepairEquipmentInSQLite.class, repairEquipmentInSQLiteDao);
        registerDao(RepairHdrInSQLite.class, repairHdrInSQLiteDao);
    }
    
    public void clear() {
        checkDataInSQLiteDaoConfig.clearIdentityScope();
        equipmentWithoutRFIDInSQLiteDaoConfig.clearIdentityScope();
        inventoryDaoConfig.clearIdentityScope();
        inventoryDetailDaoConfig.clearIdentityScope();
        materialDataInSQLiteDaoConfig.clearIdentityScope();
        repairBaseDataInSQLiteDaoConfig.clearIdentityScope();
        repairDetailInSQLiteDaoConfig.clearIdentityScope();
        repairEquipmentInSQLiteDaoConfig.clearIdentityScope();
        repairHdrInSQLiteDaoConfig.clearIdentityScope();
    }

    public CheckDataInSQLiteDao getCheckDataInSQLiteDao() {
        return checkDataInSQLiteDao;
    }

    public EquipmentWithoutRFIDInSQLiteDao getEquipmentWithoutRFIDInSQLiteDao() {
        return equipmentWithoutRFIDInSQLiteDao;
    }

    public InventoryDao getInventoryDao() {
        return inventoryDao;
    }

    public InventoryDetailDao getInventoryDetailDao() {
        return inventoryDetailDao;
    }

    public MaterialDataInSQLiteDao getMaterialDataInSQLiteDao() {
        return materialDataInSQLiteDao;
    }

    public RepairBaseDataInSQLiteDao getRepairBaseDataInSQLiteDao() {
        return repairBaseDataInSQLiteDao;
    }

    public RepairDetailInSQLiteDao getRepairDetailInSQLiteDao() {
        return repairDetailInSQLiteDao;
    }

    public RepairEquipmentInSQLiteDao getRepairEquipmentInSQLiteDao() {
        return repairEquipmentInSQLiteDao;
    }

    public RepairHdrInSQLiteDao getRepairHdrInSQLiteDao() {
        return repairHdrInSQLiteDao;
    }

}
